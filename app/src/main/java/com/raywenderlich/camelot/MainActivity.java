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

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.answerformat.TextAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.model.ConsentDocument;
import org.researchstack.backbone.model.ConsentSection;
import org.researchstack.backbone.model.ConsentSignature;
import org.researchstack.backbone.step.ConsentDocumentStep;
import org.researchstack.backbone.step.ConsentSignatureStep;
import org.researchstack.backbone.step.ConsentVisualStep;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.ui.step.layout.ConsentSignatureStepLayout;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import static android.Manifest.permission.RECORD_AUDIO;

public class MainActivity extends AppCompatActivity {

  private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

  private static final int REQUEST_CONSENT = 0;
  private static final int REQUEST_SURVEY  = 1;
  private static final int REQUEST_AUDIO = 2;

  private boolean mPermissionToRecordAccepted = false;
  private String[] mPermissions = {RECORD_AUDIO};

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Button consentButton = (Button)findViewById(R.id.consentButton);

    consentButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        displayConsent();
      }
    });

    Button surveyButton = (Button)findViewById(R.id.surveyButton);

    surveyButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        displaySurvey();
      }
    });

    Button microphoneButton = (Button)findViewById(R.id.microphoneButton);

    microphoneButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
            RECORD_AUDIO);
        if (result == PackageManager.PERMISSION_GRANTED) {
          displayAudioTask();
        } else {
          ActivityCompat.requestPermissions(MainActivity.this, mPermissions,
              REQUEST_RECORD_AUDIO_PERMISSION);
        }
      }
    });
  }
/*
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }
*/

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode){
      case REQUEST_RECORD_AUDIO_PERMISSION:
        mPermissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        displayAudioTask();
        break;
    }
    if (!mPermissionToRecordAccepted) {
      finish();
    }
  }

  private void displayConsent() {

    ConsentDocument document = createConsentDocument();

    List<Step> steps = createConsentSteps(document);

    Task consentTask = new OrderedTask("consent_task", steps);

    Intent intent = ViewTaskActivity.newIntent(this, consentTask);
    startActivityForResult(intent, REQUEST_CONSENT);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);
    // check if the request code is same as what is passed  here it is 2
    if(requestCode==REQUEST_CONSENT)
      if (resultCode == RESULT_OK) {
        {
          new ExecuteTask().execute("1", "2");
        }
      }
  }


  class ExecuteTask extends AsyncTask<String, Integer, String>
  {

    @Override
    protected String doInBackground(String... params) {

      String res=PostData(params);

      return res;
    }


  }

  public String PostData(String[] valuse) {
    String s="";
    try
    {
      HttpClient httpClient=new DefaultHttpClient();
      HttpPost httpPost=new HttpPost("http://localhost:8623/HttpPostServlet/servlet/Login");

      List<NameValuePair> list=new ArrayList<NameValuePair>();
      list.add(new BasicNameValuePair("name", valuse[0]));
      list.add(new BasicNameValuePair("pass",valuse[1]));
      httpPost.setEntity(new UrlEncodedFormEntity(list));
      HttpResponse httpResponse=  httpClient.execute(httpPost);

      HttpEntity httpEntity=httpResponse.getEntity();
      s= readResponse(httpResponse);

    }
    catch(Exception exception)  {}
    return s;


  }
  public String readResponse(HttpResponse res) {
    InputStream is=null;
    String return_text="";
    try {
      is=res.getEntity().getContent();
      BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(is));
      String line="";
      StringBuffer sb=new StringBuffer();
      while ((line=bufferedReader.readLine())!=null)
      {
        sb.append(line);
      }
      return_text=sb.toString();
    } catch (Exception e)
    {

    }
    return return_text;

  }
  private ConsentDocument createConsentDocument() {

    ConsentDocument document = new ConsentDocument();

    document.setTitle("Demo Consent");
    document.setSignaturePageTitle(R.string.rsb_consent);

    List<ConsentSection> sections = new ArrayList<>();

    sections.add(createSection(ConsentSection.Type.Overview, "Overview Info", "<h1>Read " +
        "This!</h1><p>Some " +
        "really <strong>important</strong> information you should know about this step"+
        "<p>This consent form describes the research study to help you decide if you want to participate. This form\n" +
            "provides important information about what you will be asked to do during the study, about the risks and\n" +
            "benefits of the study, and about your rights as a research subject.  "));
    sections.add(createSection(ConsentSection.Type.DataGathering, "Data Gathering Info", ""));
    sections.add(createSection(ConsentSection.Type.Privacy, "Privacy Info", ""));
    sections.add(createSection(ConsentSection.Type.DataUse, "Data Use Info", ""));
    sections.add(createSection(ConsentSection.Type.TimeCommitment, "Time Commitment Info", ""));
    sections.add(createSection(ConsentSection.Type.StudySurvey, "Study Survey Info", ""));
    sections.add(createSection(ConsentSection.Type.StudyTasks, "Study Task Info", ""));
    sections.add(createSection(ConsentSection.Type.Withdrawing, "Withdrawing Info", "Some detailed steps " +
        "to withdrawal from this study. <ul><li>Step 1</li><li>Step 2</li></ul>"));

    document.setSections(sections);

    ConsentSignature signature = new ConsentSignature();
    signature.setRequiresName(true);
    signature.setRequiresSignatureImage(true);

    document.addSignature(signature);

    document.setHtmlReviewContent("<div style=\"padding: 10px;\" class=\"header\">" +
        "<h1 style='text-align: center'>Review Consent!</h1></div>");

    return document;
  }

  private ConsentSection createSection(ConsentSection.Type type, String summary, String content) {

    ConsentSection section = new ConsentSection(type);
    section.setSummary(summary);
    section.setHtmlContent(content);

    return section;
  }

  private List<Step> createConsentSteps(ConsentDocument document) {

    List<Step> steps = new ArrayList<>();

    for (ConsentSection section: document.getSections()) {
      ConsentVisualStep visualStep = new ConsentVisualStep(section.getType().toString());
      visualStep.setSection(section);
      visualStep.setNextButtonString(getString(R.string.rsb_next));
      steps.add(visualStep);
    }

    ConsentDocumentStep documentStep = new ConsentDocumentStep("consent_doc");
    documentStep.setConsentHTML(document.getHtmlReviewContent());
    documentStep.setConfirmMessage(getString(R.string.rsb_consent_review_reason));

    steps.add(documentStep);

    ConsentSignature signature = document.getSignature(0);

    if (signature.requiresName()) {
      TextAnswerFormat format = new TextAnswerFormat();
      format.setIsMultipleLines(false);

      QuestionStep fullName = new QuestionStep("consent_name_step", "Please enter your full name",
          format);
      fullName.setPlaceholder("Full name");
      fullName.setOptional(false);
      steps.add(fullName);
    }

    if (signature.requiresSignatureImage()) {

      ConsentSignatureStep signatureStep = new ConsentSignatureStep("signature_step");
      signatureStep.setTitle(getString(R.string.rsb_consent_signature_title));
      signatureStep.setText(getString(R.string.rsb_consent_signature_instruction));
      signatureStep.setOptional(false);
      signatureStep.setStepLayoutClass(ConsentSignatureStepLayout.class);

      steps.add(signatureStep);
    }

    return steps;
  }


  private void displaySurvey()
  {
    List<Step> steps = new ArrayList<>();

    InstructionStep instructionStep = new InstructionStep("survey_instruction_step",
        "Hypertension Survey",
        "There are total 16 simple questions you need to answer. ");
    steps.add(instructionStep);

    AnswerFormat format = new TextAnswerFormat(20);

    QuestionStep nameStep = new QuestionStep("name", "What is your full name?", format);
    nameStep.setPlaceholder("Name");
    nameStep.setOptional(false);
    steps.add(nameStep);


    QuestionStep dobStep = new QuestionStep("dob", "What is your date of birth?", format);
    dobStep.setPlaceholder("MMDDYYYY");
    dobStep.setOptional(false);
    steps.add(dobStep);

    AnswerFormat ageFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle
            .SingleChoice,
            new Choice<>("17 years old and under", 0),
            new Choice<>("18-24 years old", 1),
            new Choice<>("25-34 years old", 2),
            new Choice<>("35-44 years old", 3),
            new Choice<>("55-64 years old", 4),
            new Choice<>("65-74 years old", 5),
            new Choice<>("75 years or older", 6));

    QuestionStep ageStep = new QuestionStep("age", "What is your age?", ageFormat);
    ageStep.setPlaceholder(" ");
    ageStep.setOptional(false);
    steps.add(ageStep);

    QuestionStep addressStep = new QuestionStep("address", "What is your address", format);
    addressStep.setPlaceholder("Enter your address");
    addressStep.setOptional(false);
    steps.add(addressStep);

    QuestionStep phoneStep = new QuestionStep("phone", "What is your phone number", format);
    phoneStep.setPlaceholder("XXXXXXXXXX");
    phoneStep.setOptional(false);
    steps.add(phoneStep);

    AnswerFormat genderFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle
        .SingleChoice,
        new Choice<>("Female", 0),
        new Choice<>("Male", 1));

    QuestionStep genderStep = new QuestionStep("gender", "What is your gender?", genderFormat);
    genderStep.setPlaceholder(" ");
    genderStep.setOptional(false);
    steps.add(genderStep);

    AnswerFormat raceFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle
            .SingleChoice,
            new Choice<>("White", 0),
            new Choice<>("Hispanic or Latino", 1),
            new Choice<>("Black or African American", 2),
            new Choice<>("Native American or American Indian", 3),
            new Choice<>("Asian / Pacific Islander", 4),
            new Choice<>("Other", 5));

    QuestionStep raceStep = new QuestionStep("race", "What is your ethnicity?", raceFormat);
    raceStep.setPlaceholder(" ");
    raceStep.setOptional(false);
    steps.add(raceStep);

    AnswerFormat incomeFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle
            .SingleChoice,
            new Choice<>("Less than 60000", 0),
            new Choice<>("60000 to 79999", 1),
            new Choice<>("80000 to 99999", 2),
            new Choice<>("100000 to 149999", 3),
            new Choice<>("150000 or more", 4));

    QuestionStep incomeStep = new QuestionStep("income", "What is your income?", incomeFormat);
    incomeStep.setPlaceholder(" ");
    incomeStep.setOptional(false);
    steps.add(incomeStep);

    QuestionStep weightStep = new QuestionStep("weight", "What is your weight(in lb)", format);
    weightStep.setPlaceholder("for example 133");
    weightStep.setOptional(false);
    steps.add(weightStep);

    QuestionStep heightStep = new QuestionStep("height", "What is your height", format);
    heightStep.setPlaceholder("for example 5'7");
    heightStep.setOptional(false);
    steps.add(heightStep);

    AnswerFormat healthFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle
            .SingleChoice,
            new Choice<>("1", 0),
            new Choice<>("2", 1),
            new Choice<>("3", 2),
            new Choice<>("4", 3),
            new Choice<>("5", 4));

    QuestionStep healthStep = new QuestionStep("health", "How do you rate your health condition? (1 means poor, 5 means very good)", healthFormat);
    healthStep.setPlaceholder(" ");
    healthStep.setOptional(false);
    steps.add(healthStep);

    AnswerFormat symptomFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle
            .SingleChoice,
            new Choice<>("Yes", 0),
            new Choice<>("No", 1));

    QuestionStep symptomStep = new QuestionStep("symptom", "Have you experienced any symptom of hypertension?", symptomFormat);
    symptomStep.setPlaceholder(" ");
    symptomStep.setOptional(false);
    steps.add(symptomStep);

    AnswerFormat exerciseFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle
            .SingleChoice,
            new Choice<>("Daily", 0),
            new Choice<>("2 times a week", 1),
            new Choice<>("3 times or more a week ", 2),
            new Choice<>("infrequent", 3));

    QuestionStep exerciseStep = new QuestionStep("exercise", "How often do you exercise", exerciseFormat);
    exerciseStep.setPlaceholder(" ");
    exerciseStep.setOptional(false);
    steps.add(exerciseStep);

    AnswerFormat doctorFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle
            .SingleChoice,
            new Choice<>("less tham 1 month", 0),
            new Choice<>("1 month to 3 months", 1),
            new Choice<>("3 months to 1 year ", 2),
            new Choice<>("more than 1 year ", 3),
            new Choice<>("never", 4));

    QuestionStep doctorStep = new QuestionStep("doctor", "When did you last visit a doctor for hypertension?", doctorFormat);
    doctorStep.setPlaceholder(" ");
    doctorStep.setOptional(false);
    steps.add(doctorStep);

    AnswerFormat medicineFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle
            .SingleChoice,
            new Choice<>("Yes", 0),
            new Choice<>("No", 1));

    QuestionStep medicineStep = new QuestionStep("medicine", "Do you take any medicine for hypertension?", medicineFormat);
    medicineStep.setPlaceholder(" ");
    medicineStep.setOptional(false);
    steps.add(medicineStep);

    AnswerFormat medFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle
            .SingleChoice,
            new Choice<>("lisinopril oral", 0),
            new Choice<>("atenolol oral", 1),
            new Choice<>("Bystolic oral", 2),
            new Choice<>("Diovan oral", 3),
            new Choice<>("hydrochlorothiazide oral", 4),
            new Choice<>("Other", 4),
            new Choice<>("None", 4));

    QuestionStep medStep = new QuestionStep("med", "If you do take medicine for hypertension, which medicine do you take? (If don't take any medicine, select None)", medFormat);
    medStep.setPlaceholder(" ");
    medStep.setOptional(false);
    steps.add(medStep);

    AnswerFormat insuranceFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle
            .SingleChoice,
            new Choice<>("Yes", 0),
            new Choice<>("No", 1));

    QuestionStep insuranceStep = new QuestionStep("insurance", "Do you have medical insurance?", insuranceFormat);
    insuranceStep.setPlaceholder(" ");
    insuranceStep.setOptional(false);
    steps.add(insuranceStep);

    AnswerFormat willFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle
            .SingleChoice,
            new Choice<>("Yes", 0),
            new Choice<>("No", 1),
            new Choice<>("Not sure", 2));

    QuestionStep willStep = new QuestionStep("will", "Are you willing to participate in the clinical trial?", willFormat);
    willStep.setPlaceholder(" ");
    willStep.setOptional(false);
    steps.add(willStep);


    /*
    AnswerFormat colorAnswerFormat = new ImageChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle
        .SingleChoice,
        new Choice<>("Red", R.drawable.red_selector),
        new Choice<>("Orange", R.drawable.orange_selector),
        new Choice<>("Yellow", R.drawable.yellow_selector),
        new Choice<>("Green", R.drawable.green_selector),
        new Choice<>("Blue", R.drawable.blue_selector),
        new Choice<>("Purple", R.drawable.purple_selector));

    QuestionStep colorStep = new QuestionStep("color_step", "What is your favorite color?", colorAnswerFormat);
    colorStep.setOptional(false);
    steps.add(colorStep);
*/

    InstructionStep summaryStep = new InstructionStep("survey_summary_step",
        "Congratulation! You have finished the survey. ",
        "You will be informed whethere you are eligiable for the hypertension trail, then you can proceed with consent");
    steps.add(summaryStep);

    OrderedTask task = new OrderedTask("survey_task", steps);

    Intent intent = ViewTaskActivity.newIntent(this, task);
    startActivityForResult(intent, REQUEST_SURVEY);

    TaskResult taskresult= new TaskResult("survey_task");

  }



  private void displayAudioTask()
  {
    List<Step> steps = new ArrayList<>();

    InstructionStep instructionStep = new InstructionStep("audio_instruction_step",
        "A sentence prompt will be given to you to read.",
        "These are the last dying words of Joseph of Aramathea.");
    steps.add(instructionStep);

    AudioStep audioStep = new AudioStep("audio_step");
    audioStep.setTitle("Repeat the following phrase:");
    audioStep.setText("The Holy Grail can be found in the Castle of Aaaaaaaaaaah");
    audioStep.setDuration(10);
    steps.add(audioStep);

    InstructionStep summaryStep = new InstructionStep("audio_summary_step",
        "Right. Off you go!",
        "That was easy!");
    steps.add(summaryStep);

    OrderedTask task = new OrderedTask("audio_task", steps);

    Intent intent = ViewTaskActivity.newIntent(this, task);
    startActivityForResult(intent, REQUEST_AUDIO);
  }

}
