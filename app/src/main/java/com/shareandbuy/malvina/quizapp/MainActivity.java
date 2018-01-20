package com.shareandbuy.malvina.quizapp;

import android.content.Context;
import android.content.res.Resources;
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
                                           }}));

        questionList.add(new QuestionBlock(getString(R.string.q_2_text)
                                            , getString(R.string.q_2_correct_answear)
                                            , new ArrayList<String>(){{
                                                for(String str : getResources().getStringArray(R.array.q_2_wrong_answears)) {
                                                    add(str);
                                                }
                                            }}));
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
                                           }}));
        questionList.add(new QuestionBlock(getString(R.string.q_4_text)
                                            , getString(R.string.q_4_correct_answear)
                                            , new ArrayList<String>(){{
                                            for(String str : getResources().getStringArray(R.array.q_4_wrong_answears)) {
                                                add(str);
                                            }
                                            }}));

        for(QuestionBlock block: questionList){
            block.build(this, quizBody);
        }
    }

    public void checkAnswears(View view){
        for(QuestionBlock block : questionList){
            block.verify();
        }
    }
}

class QuestionBlock{
    String question;
    String correctAnswear;
    ArrayList<String> correctAnswearList;
    ArrayList<String> wrongAnswearsList;
    boolean isRadioButton = false;
    boolean isCheckBox = false;
    Answears answears;
    LinearLayout questionLayout;

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
        this.questionLayout = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.question_layout, null);
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
        //Include question layout to body of quiz layout
        quizbody.addView(questionLayout);
    }

    void verify(){
        Context context = this.questionLayout.getContext();
        Resources res = context.getResources();
        if(isRadioButton){
            if(answears.compareChecked(this.correctAnswear)){
                this.questionLayout.setBackgroundColor(res.getColor(R.color.correct_answear_color));
            }
            else{
                this.questionLayout.setBackgroundColor(res.getColor(R.color.wrong_answear_color));
            }
        }
    }
}

class Answears {

    RadioGroup radioGroup = null;
    LinearLayout checkboxGroup = null;
    Context context;
    ArrayList<RadioButton> radioButtonList;
    ArrayList<CheckBox> checkBoxList;

    Answears(RadioGroup group, ArrayList<String> answears){
        this.radioButtonList = new ArrayList<RadioButton>();
        this.context = group.getContext();
        this.radioGroup = group;
        for (String answear : answears) {
            RadioButton temp_button = (RadioButton)LayoutInflater.from(context).inflate(R.layout.radio_button_view, null);
            temp_button.setText(answear);
            this.radioButtonList.add(temp_button);
            group.addView(temp_button);
        }
    }

    Answears(LinearLayout group, ArrayList<String> answears){
        this.checkBoxList = new ArrayList<CheckBox>();
        this.context = group.getContext();
        this.checkboxGroup = group;
        for (String answear : answears) {
            CheckBox temp_checkBox = (CheckBox)LayoutInflater.from(context).inflate(R.layout.checkbox_view, null);
            temp_checkBox.setText(answear);
            group.addView(temp_checkBox);
            this.checkBoxList.add(temp_checkBox);
        }
    }

    boolean compareChecked(String correctAnswear){
        for (RadioButton button : this.radioButtonList){
            if (button.isChecked()) {
                if (button.getText().toString().equals(correctAnswear)) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean compareChecked(ArrayList<String> correctAnswears){
        ArrayList<String> checkedAnswearsList = new ArrayList<String>();
        for (CheckBox checkBox : this.checkBoxList) {
            if (checkBox.isChecked()) {
                checkedAnswearsList.add(checkBox.getText().toString());
            }
        }
        Collections.sort(checkedAnswearsList);
        Collections.sort(correctAnswears);
        if(checkedAnswearsList.equals(correctAnswears)){
            return true;
        }
        return false;
    }
}