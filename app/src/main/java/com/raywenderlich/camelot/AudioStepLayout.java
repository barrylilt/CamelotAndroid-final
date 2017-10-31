/*
 * Copyright (c) 2016 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.camelot;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.utils.FileUtils;

import java.io.File;

public class AudioStepLayout extends RelativeLayout implements StepLayout
{
  public static final String KEY_AUDIO = "AudioStep.Audio";

  private StepCallbacks mStepCallbacks;
  private AudioStep mStep;
  private StepResult<String> mResult;
  private boolean mIsRecordingComplete = false;
  private String mFilename;

  public AudioStepLayout(Context context)
  {
    super(context);
  }

  public AudioStepLayout(Context context, AttributeSet attrs)
  {
    super(context, attrs);
  }

  public AudioStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
  {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public void initialize(Step step, StepResult result)
  {
    this.mStep = (AudioStep)step;
    this.mResult = result == null ? new StepResult<>(step) : result;

    initializeStep();
  }

  @Override
  public View getLayout()
  {
    return this;
  }

  @Override
  public boolean isBackEventConsumed()
  {
    setDataToResult();
    mStepCallbacks.onSaveStep(StepCallbacks.ACTION_PREV, mStep, mResult);
    return false;
  }

  @Override
  public void setCallbacks(StepCallbacks callbacks)
  {
    this.mStepCallbacks = callbacks;
  }

  private void initializeStep()
  {
    LayoutInflater.from(getContext())
        .inflate(R.layout.audio_step_layout, this, true);

    TextView title = (TextView) findViewById(R.id.title);
    title.setText(mStep.getTitle());

    TextView text = (TextView) findViewById(R.id.summary);
    text.setText(mStep.getText());

    final TextView countdown = (TextView) findViewById(R.id.countdown);
    countdown.setText("Seconds remaining: " + Integer.toString(mStep.getDuration()));

    final TextView countdown_title = (TextView) findViewById(R.id.countdown_title);

    final Button beginButton = (Button) findViewById(R.id.begin_recording);

    beginButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {

        mFilename = getContext().getFilesDir().getAbsolutePath();
        mFilename += "/camelotaudiorecord.3gp";

        final AudioRecorder audioRecorder = new AudioRecorder();
        audioRecorder.startRecording(mFilename);

        beginButton.setVisibility(GONE);
        countdown_title.setVisibility(View.VISIBLE);

        CountDownTimer Count = new CountDownTimer(mStep.getDuration()*1000, 1000) {
          public void onTick(long millisUntilFinished) {
            countdown.setText("Seconds remaining: " + millisUntilFinished / 1000);
          }

          public void onFinish() {

            mIsRecordingComplete = true;

            audioRecorder.stopRecording();

            AudioStepLayout.this.setDataToResult();
            mStepCallbacks.onSaveStep(StepCallbacks.ACTION_NEXT, mStep, mResult);
          }
        };

        Count.start();
      }
    });
  }

  private void setDataToResult()
  {
    mResult.setResultForIdentifier(KEY_AUDIO, getBase64EncodedAudio());
  }

  private String getBase64EncodedAudio()
  {
    if(mIsRecordingComplete)
    {
      File file = new File(mFilename);

      try {
        byte[] bytes = FileUtils.readAll(file);

        String encoded = Base64.encodeToString(bytes, Base64.DEFAULT);
        return encoded;

      } catch (Exception e) {
        return null;
      }
    }
    else
    {
      return null;
    }
  }

}
