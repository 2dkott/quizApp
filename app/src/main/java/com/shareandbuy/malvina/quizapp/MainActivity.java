package com.shareandbuy.malvina.quizapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<String> wrongAncwears  = new ArrayList<String>();
        wrongAncwears.add("Peter");
        wrongAncwears.add("Vasya");
        LinearLayout quizBody = (LinearLayout) findViewById(R.id.quizbody);

        QuestionBlock newquestion = new QuestionBlock("What is my name?"
                                                      , "Konstantin"
                                                      , wrongAncwears);

        newquestion.build(this, quizBody);

        QuestionBlock newquestion_1 = new QuestionBlock("What is my name?"
                , wrongAncwears
                , wrongAncwears);
        newquestion_1.build(this, quizBody);
    }
}

class QuestionBlock{
    String question;
    String correctAnswear;
    ArrayList<String> correctAnswearList;
    ArrayList<Answears> answearsViewList = new ArrayList<Answears>();
    ArrayList<String> wrongAnswearsList;
    boolean isRadioButton = false;
    boolean isCheckBox = false;
    RadioGroup radioButtonContainer;
    LinearLayout checkBoxContainer;
    Answears answears;

    QuestionBlock(String question, String correctAnswear, ArrayList<String> wrongAnswearsList){
        this.question = question;
        this.correctAnswear = correctAnswear;
        this.wrongAnswearsList = wrongAnswearsList;
        isRadioButton = true;

    }

    QuestionBlock(String question, ArrayList<String> correctAnswearList, ArrayList<String> wrongAnswearsList){
        this.question = question;
        this.correctAnswearList = correctAnswearList;
        this.wrongAnswearsList = wrongAnswearsList;
        isCheckBox = true;
    }

    void build(Context context, LinearLayout quizbody){
        //Define view we need to build a question
        LinearLayout questionLayout = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.question_layout, null);
        ImageView imageView = questionLayout.findViewWithTag(context.getString(R.string.tag_question_image));
        TextView questionTextView = questionLayout.findViewWithTag(context.getString(R.string.tag_question));
        ArrayList<String> answearsList = this.wrongAnswearsList;
        if(isRadioButton) {
            answearsList.add(this.correctAnswear);
            answears = new Answears((RadioGroup)questionLayout.findViewWithTag(context.getString(R.string.tag_question_radio_button_group))
                                   , answearsList);
        }
        if(isCheckBox) {
            answearsList.addAll(correctAnswearList);
            answears = new Answears((LinearLayout)questionLayout.findViewWithTag(context.getString(R.string.tag_question_checkbox_group))
                                    , answearsList);
        }
        //Build question layout with text of question and list of answears
        //according is that check box or radio button
        /*
        if(isRadioButton) {
            answearsList.add(this.correctAnswear);
            radioButtonContainer = questionLayout.findViewWithTag(context.getString(R.string.tag_question_radio_button_group));
            for (String answear : answearsList) {
                RadioButton temp_button = (RadioButton)LayoutInflater.from(context).inflate(R.layout.radio_button_view, null);
                temp_button.setText(answear);
                this.answearsViewList.add(new Answears(temp_button));
                radioButtonContainer.addView(temp_button);
            }
        }
        if(isCheckBox) {
            answearsList.addAll(correctAnswearList);
            checkBoxContainer = questionLayout.findViewWithTag(context.getString(R.string.tag_question_checkbox_group));
            for (String answear : answearsList) {
                CheckBox temp_checkBox = (CheckBox)LayoutInflater.from(context).inflate(R.layout.checkbox_view, null);
                temp_checkBox.setText(answear);
                this.answearsViewList.add(new Answears(temp_checkBox));
                checkBoxContainer.addView(temp_checkBox);
            }
        }
        */

        //Include question layout to body of quiz layout
        quizbody.addView(questionLayout);
    }

    void verify(){
        for(Answears answear: answearsViewList){
            if(answear.isChecked()){

            }
        }
    }
}

class Answears {

    RadioGroup radioGroup = null;
    LinearLayout checkboxGroup = null;
    Context context;

    Answears(RadioGroup group, ArrayList<String> answears){
        context = group.getContext();
        this.radioGroup = group;
        for (String answear : answears) {
            RadioButton temp_button = (RadioButton)LayoutInflater.from(context).inflate(R.layout.radio_button_view, null);
            temp_button.setText(answear);
            group.addView(temp_button);;
        }
    }

    Answears(LinearLayout group, ArrayList<String> answears){
        context = group.getContext();
        this.checkboxGroup = group;
        for (String answear : answears) {
            CheckBox temp_checkBox = (CheckBox)LayoutInflater.from(context).inflate(R.layout.checkbox_view, null);
            temp_checkBox.setText(answear);
            group.addView(temp_checkBox);;
        }
    }

    boolean compareChecked(String correctAnswear){
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton checkedButton = (RadioButton)context.find
    }

}