package com.shareandbuy.malvina.quizapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    ArrayList<QuestionBlock> questionList = new ArrayList<QuestionBlock>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout quizBody = (LinearLayout) findViewById(R.id.quizbody);

        questionList.add(new QuestionBlock(getString(R.string.q_1_text)
                                           , getString(R.string.q_1_correct_answear)
                                           , new ArrayList<String>(){{
                                               for(String str : getResources().getStringArray(R.array.q_1_wrong_answears)) {
                                                   add(str);
                                               }
                                           }}).addImage(R.drawable.q_1_image)
                                           );

        questionList.add(new QuestionBlock(getString(R.string.q_2_text)
                                            , getString(R.string.q_2_correct_answear)
                                            , new ArrayList<String>(){{
                                                for(String str : getResources().getStringArray(R.array.q_2_wrong_answears)) {
                                                    add(str);
                                                }
                                            }}).addImage(R.drawable.q_2_image)
                                            );
        questionList.add(new QuestionBlock(getString(R.string.q_3_text)
                                           , new ArrayList<String>(){{
                                               for(String str : getResources().getStringArray(R.array.q_3_correct_answears)) {
                                                   add(str);
                                               }
                                           }}
                                           , new ArrayList<String>(){{
                                               for(String str : getResources().getStringArray(R.array.q_3_wrong_answears)) {
                                                   add(str);
                                               }
                                           }}).addImage(R.drawable.q_3_image)
                                           );
        questionList.add(new QuestionBlock(getString(R.string.q_4_text)
                                           , getString(R.string.q_4_correct_answear)
                                           , new ArrayList<String>(){{
                                               for(String str : getResources().getStringArray(R.array.q_4_wrong_answears)) {
                                                   add(str);
                                               }
                                           }}).addImage(R.drawable.q_4_image)
                                           );

        for(QuestionBlock block: questionList){
            block.build(this, quizBody);
        }
    }

    public void checkAnswears(View view){
        for(QuestionBlock block : questionList){
            block.verify();
        }
    }

    public void sendResult(View view){
        String body="";
        for(QuestionBlock block : this.questionList){
            body += block.getState();
        }
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Test subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(emailIntent, "Chooser Title"));
    }
}

class QuestionBlock{
    String question;
    String correctAnswer;
    ArrayList<String> correctAnswerList =  null;
    ArrayList<String> wrongAnswersList;
    boolean isRadioButton = false;
    boolean isCheckBox = false;
    Answers answears;
    LinearLayout questionLayout;
    int imageId = 0;
    TextView questionTextView;
    boolean hasCorrectAnswer=false;
    Context context;
    Resources res;
    String selectedAnswer;

    QuestionBlock(String question, String correctAnswear, ArrayList<String> wrongAnswearsList){
        this.question = question;
        this.correctAnswer = correctAnswear;
        this.wrongAnswersList = wrongAnswearsList;
        this.isRadioButton = true;
    }

    QuestionBlock(String question, ArrayList<String> correctAnswearList, ArrayList<String> wrongAnswearsList){
        this.question = question;
        this.correctAnswerList = correctAnswearList;
        this.wrongAnswersList = wrongAnswearsList;
        this.isCheckBox = true;
    }

    QuestionBlock addImage(int imageId){
        this.imageId = imageId;
        return this;
    }

    void build(Context context, LinearLayout quizbody){
        //Define view we need to build a question
        this.questionLayout = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.question_layout, null);
        this.context = this.questionLayout.getContext();
        this.res = context.getResources();
        ArrayList<String> answearsList = this.wrongAnswersList;
        this.questionTextView = (TextView)questionLayout.findViewWithTag(context.getString(R.string.tag_question));
        this.questionTextView.setText(this.question);
        ImageView image = (ImageView)questionLayout.findViewWithTag(context.getString(R.string.tag_question_image));
        if(this.imageId!=0){
            image.setImageResource(this.imageId);
        }
        if(this.isRadioButton) {
            answearsList.add(this.correctAnswer);
            this.answears = new Answers((RadioGroup)questionLayout.findViewWithTag(context.getString(R.string.tag_question_radio_button_group))
                                   , answearsList);
        }
        if(this.isCheckBox) {
            answearsList.addAll(correctAnswerList);
            this.answears = new Answers((LinearLayout)questionLayout.findViewWithTag(context.getString(R.string.tag_question_checkbox_group))
                                    , answearsList);
        }
        //Include question layout to body of quiz layout
        quizbody.addView(questionLayout);
    }

    void verify(){

        if(this.isRadioButton){
            if(this.answears.compareChecked(this.correctAnswer)){
                this.questionTextView.setBackgroundColor(this.res.getColor(R.color.correct_answear_color));
                this.hasCorrectAnswer=true;
            }
            else{
                this.questionTextView.setBackgroundColor(this.res.getColor(R.color.wrong_answear_color));
            }
        }
        if(this.isCheckBox){
            if(this.answears.compareChecked(this.correctAnswerList)){
                this.questionTextView.setBackgroundColor(this.res.getColor(R.color.correct_answear_color));
                this.hasCorrectAnswer=true;
            }
            else{
                this.questionTextView.setBackgroundColor(this.res.getColor(R.color.wrong_answear_color));
            }
        }
    }

    String getState(){
        if(this.correctAnswerList != null) {
            StringBuilder tempAnswerResult = new StringBuilder();
            for (int i = 0; i <= this.correctAnswerList.size(); i++) {
                tempAnswerResult.append(this.correctAnswerList.get(i));
                if (i != this.correctAnswerList.size()) {
                    tempAnswerResult.append("\n");
                }
            }
            this.correctAnswer = tempAnswerResult.toString();
        }

        String resultString = this.res.getString(R.string.result_question) + ": " +
                                "\n" + question +
                                "\n" + this.res.getString(R.string.result_correct_answer) + ": " +
                                "\n" + this.correctAnswer +
                                "\n" + this.res.getString(R.string.result_selected_answer) + ": " +
                                "\n" + answears.getSelectedAnswer();
        return resultString;
    }

    boolean hasCorrectAnswer(){
        return this.hasCorrectAnswer;
    }
}

class Answers {

    RadioGroup radioGroup = null;
    LinearLayout checkboxGroup = null;
    Context context;
    ArrayList<RadioButton> radioButtonList;
    ArrayList<CheckBox> checkBoxList;
    String selectedAnswer = "";

    Answers(RadioGroup group, ArrayList<String> answers){
        this.radioButtonList = new ArrayList<RadioButton>();
        this.context = group.getContext();
        this.radioGroup = group;
        for (String answer : answers) {
            RadioButton temp_button = (RadioButton)LayoutInflater.from(context).inflate(R.layout.radio_button_view, null);
            temp_button.setText(answer);
            this.radioButtonList.add(temp_button);
            group.addView(temp_button);
        }
    }

    Answers(LinearLayout group, ArrayList<String> answers){
        this.checkBoxList = new ArrayList<CheckBox>();
        this.context = group.getContext();
        this.checkboxGroup = group;
        for (String answer : answers) {
            CheckBox temp_checkBox = (CheckBox)LayoutInflater.from(context).inflate(R.layout.checkbox_view, null);
            temp_checkBox.setText(answer);
            group.addView(temp_checkBox);
            this.checkBoxList.add(temp_checkBox);
        }
    }

    boolean compareChecked(String correctAnswer){
        for (RadioButton button : this.radioButtonList){
            if (button.isChecked()) {
                this.selectedAnswer = button.getText().toString();
                if (button.getText().toString().equals(correctAnswer)) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean compareChecked(ArrayList<String> correctAnswers){
        ArrayList<String> checkedAnswearsList = new ArrayList<String>();
        for (CheckBox checkBox : this.checkBoxList) {
            if (checkBox.isChecked()) {
                checkedAnswearsList.add(checkBox.getText().toString());
            }
        }
        StringBuilder tempAnswerResult = new StringBuilder();
        for(int i = 0; i<checkedAnswearsList.size(); i++){
            tempAnswerResult.append(checkedAnswearsList.get(i));
            if(i!=(checkedAnswearsList.size()-1)){
                tempAnswerResult.append("\n");
            }
        }
        this.selectedAnswer = tempAnswerResult.toString();
        Collections.sort(checkedAnswearsList);
        Collections.sort(correctAnswers);
        if(checkedAnswearsList.equals(correctAnswers)){
            return true;
        }
        return false;
    }
    String getSelectedAnswer(){
        if(this.selectedAnswer.isEmpty())
            return this.context.getResources().getString(R.string.result_nothing);
        else return this.selectedAnswer;
    }
}