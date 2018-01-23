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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    ArrayList<QuestionBlock> questionList = new ArrayList<QuestionBlock>();
    boolean quizIsVerified = false;

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

    //Verify answers
    public void checkAnswears(View view){
        quizIsVerified = true;
        for(QuestionBlock block : questionList){
            block.verify();
        }
    }
    //Collecting statistic of questions and send it to email app
    public void sendResult(View view){
        EditText userNameView = (EditText)findViewById(R.id.user_name);
        if(userNameView.getText().toString().isEmpty()){
            Toast toast = Toast.makeText(this, getString(R.string.check_user_name_first), Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if(!quizIsVerified){
            Toast toast = Toast.makeText(this, getString(R.string.check_answers_first), Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(userNameView.getText().toString());
        strBuilder.append(getString(R.string.result_title));
        strBuilder.append("\n\n");
        for(QuestionBlock block : this.questionList){
            strBuilder.append(block.getState());
            strBuilder.append("\n");
        }
        String body= strBuilder.toString();
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Test subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(emailIntent, "Chooser Title"));
    }
}

class QuestionBlock{
    private String question;
    private String correctAnswer;
    private ArrayList<String> correctAnswerList =  null;
    private ArrayList<String> wrongAnswersList;
    private boolean isRadioButton = false;
    private boolean isCheckBox = false;
    private Answers answears;
    private LinearLayout questionLayout;
    private int imageId = 0;
    private TextView questionTextView;
    private Resources res;

    //Build question with radio button group of answers
    QuestionBlock(String question, String correctAnswear, ArrayList<String> wrongAnswearsList){
        this.question = question;
        this.correctAnswer = correctAnswear;
        this.wrongAnswersList = wrongAnswearsList;
        this.isRadioButton = true;
    }

    //Build question with checkbox group of answers
    QuestionBlock(String question, ArrayList<String> correctAnswearList, ArrayList<String> wrongAnswearsList){
        this.question = question;
        this.correctAnswerList = correctAnswearList;
        this.wrongAnswersList = wrongAnswearsList;
        this.isCheckBox = true;
    }

    //Setting image of question
    QuestionBlock addImage(int imageId){
        this.imageId = imageId;
        return this;
    }

    void build(Context context, LinearLayout quizbody){
        //Define views we need to build a question bloc

        //Setting basic question block
        this.questionLayout = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.question_layout, null);

        //Getting assess to Resources through current context
        this.res = context.getResources();

        //Setting container for question text
        this.questionTextView = (TextView)questionLayout.findViewWithTag(context.getString(R.string.tag_question));
        this.questionTextView.setText(this.question);

        //Setting image container for question
        ImageView image = (ImageView)questionLayout.findViewWithTag(context.getString(R.string.tag_question_image));
        if(this.imageId!=0){
            image.setImageResource(this.imageId);
        }

        //Building question block for question
        ArrayList<String> answearsList = this.wrongAnswersList;

        //In case of radio group we have onle one correct answer
        if(this.isRadioButton) {
            answearsList.add(this.correctAnswer);
            this.answears = new Answers((RadioGroup)questionLayout.findViewWithTag(context.getString(R.string.tag_question_radio_button_group))
                                   , answearsList);
        }
        //In case of checkbox group we could have a few correct answers
        if(this.isCheckBox) {
            answearsList.addAll(correctAnswerList);
            this.answears = new Answers((LinearLayout)questionLayout.findViewWithTag(context.getString(R.string.tag_question_checkbox_group))
                                    , answearsList);
        }
        //Include question layout to body of quiz layout
        quizbody.addView(questionLayout);
    }

    void verify(){
        //Correct answer is marked with green color and wrong one with rose
        //Checking that answer block is group of radio buttons, so correct answer is a single one
        if(this.isRadioButton){
            if(this.answears.compareChecked(this.correctAnswer)){
                this.questionTextView.setBackgroundColor(this.res.getColor(R.color.correct_answear_color));
            }
            else{
                this.questionTextView.setBackgroundColor(this.res.getColor(R.color.wrong_answear_color));
            }
        }
        //Checking that answer block is group of checkboxes, so it might be a few correct answers
        if(this.isCheckBox){
            if(this.answears.compareChecked(this.correctAnswerList)){
                this.questionTextView.setBackgroundColor(this.res.getColor(R.color.correct_answear_color));
            }
            else{
                this.questionTextView.setBackgroundColor(this.res.getColor(R.color.wrong_answear_color));
            }
        }
    }

    String getState(){
        //Getting info of question like question text, correct answers and selected answers
        //In case of radio button group we already have single string but for list of correct answers(checkbox group)
        //we have a list of correct answers
        if(this.correctAnswerList != null) {
            StringBuilder strBuilder = new StringBuilder();
            for (int i = 0; i < this.correctAnswerList.size(); i++) {
                strBuilder.append("\"");
                strBuilder.append(this.correctAnswerList.get(i));
                strBuilder.append("\"");
                if (i != (this.correctAnswerList.size()-1)) {
                    strBuilder.append("\n");
                }
            }
            this.correctAnswer = strBuilder.toString();
        }
        else this.correctAnswer = "\"" + this.correctAnswer + "\"";

        //Building a single string of question details
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(this.res.getString(R.string.result_question));
        strBuilder.append(": ");
        strBuilder.append("\n");
        strBuilder.append("\"");
        strBuilder.append(this.question);
        strBuilder.append("\"");
        strBuilder.append("\n");
        strBuilder.append(this.res.getString(R.string.result_correct_answer));
        strBuilder.append(": ");
        strBuilder.append("\n");
        strBuilder.append(this.correctAnswer);
        strBuilder.append("\n");
        strBuilder.append(this.res.getString(R.string.result_selected_answer));
        strBuilder.append(": ");
        strBuilder.append("\n");
        strBuilder.append(answears.getSelectedAnswersText());
        strBuilder.append("\n");

        return strBuilder.toString();
     }
}

class Answers {

    private RadioGroup radioGroup = null;
    private LinearLayout checkboxGroup = null;
    private Context context;
    private ArrayList<RadioButton> radioButtonList;
    private ArrayList<CheckBox> checkBoxList;
    private String selectedAnswer = "";

    //Build answer block as radio button group
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
    //Build answer block as checkbox group
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

    //Compare correct answer with answer was checked if it is same it return true else false
    boolean compareChecked(String correctAnswer){
        for (RadioButton button : this.radioButtonList){
            if (button.isChecked()) {
                this.selectedAnswer = "\"" + button.getText().toString() + "\"";
                if (button.getText().toString().equals(correctAnswer)) {
                    return true;
                }
            }
        }
        return false;
    }

    //Compare correct answer list with a list of answers were checked if it is same it return true else false
    boolean compareChecked(ArrayList<String> correctAnswers){
        ArrayList<String> checkedAnswearsList = new ArrayList<String>();
        for (CheckBox checkBox : this.checkBoxList) {
            if (checkBox.isChecked()) {
                checkedAnswearsList.add(checkBox.getText().toString());
            }
        }
        StringBuilder strBuilder = new StringBuilder();
        for(int i = 0; i<checkedAnswearsList.size(); i++){
            strBuilder.append("\"");
            strBuilder.append(checkedAnswearsList.get(i));
            strBuilder.append("\"");
            if(i!=(checkedAnswearsList.size()-1)){
                strBuilder.append("\n");
            }
        }
        this.selectedAnswer = strBuilder.toString();
        Collections.sort(checkedAnswearsList);
        Collections.sort(correctAnswers);
        if(checkedAnswearsList.equals(correctAnswers)){
            return true;
        }
        return false;
    }

    //Return a text of selected answer
    String getSelectedAnswersText(){
        if(this.selectedAnswer.isEmpty())
            return this.context.getResources().getString(R.string.result_nothing);
        else return this.selectedAnswer;
    }
}