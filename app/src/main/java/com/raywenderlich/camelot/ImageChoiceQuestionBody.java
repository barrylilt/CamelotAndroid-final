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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.SingleChoiceQuestionBody;

public class ImageChoiceQuestionBody <T> extends SingleChoiceQuestionBody
{
  private Choice[] mChoices;

  public ImageChoiceQuestionBody(Step step, StepResult result) {
    super(step, result);

    QuestionStep questionStep = (QuestionStep)step;
    ImageChoiceAnswerFormat format = (ImageChoiceAnswerFormat)questionStep.getAnswerFormat();
    mChoices = format.getChoices();
  }

  @Override
  public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent)
  {
    RadioGroup group = (RadioGroup)super.getBodyView(viewType, inflater, parent);

    for (int i = 0; i < mChoices.length; i++) {

      RadioButton button = (RadioButton)group.getChildAt(i);
      button.setButtonDrawable(Integer.parseInt(mChoices[i].getValue().toString()));
    }

    return group;
  }
}
