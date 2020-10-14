package com.example.w5_p3;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

import static java.lang.Math.abs;

public class TopFragment extends Fragment {

    private TextView tvWord;
    private Button clear;
    private Button submit;
    private int scoreCounter;
    private LinearLayout gridButtons;
    private String answer;
    private HashSet<String> wordsSet;
    private ScoreListener main_class;
    private String[] words;
    private Button previousButton;


    final private String vowels = "aeiou";
    final private String doublescore = "szpxq";
    final private String file_name = "words.txt";

    public TopFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Read dictionary into HashSet
        // First split into String array then add each String
        InputStream wordListContents = null;
        try {
            wordListContents = context.getResources().getAssets().open(file_name);
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (wordListContents, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) Character.toLowerCase(c));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        words = textBuilder.toString().split("\n");

        generateDictionary();

        main_class = (ScoreListener) context;
    }

    private void generateDictionary() {
        wordsSet = new HashSet<>();
        for (String s : words) {
            wordsSet.add(s.substring(0, s.length() - 1));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top, container, false);
        tvWord = (TextView) view.findViewById(R.id.tvWord);
        clear = (Button) view.findViewById(R.id.btnClear);
        submit = (Button) view.findViewById(R.id.btnSubmit);
        gridButtons = view.findViewById(R.id.buttonGrid);

        tvWord.setBackgroundColor(Color.YELLOW);

        generateLetterGrid();

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentWord = tvWord.getText().toString();
                if (currentWord.isEmpty())
                    Toast.makeText(getContext(), "Please enter a word", Toast.LENGTH_SHORT).show();
                else {
                    wordScore(currentWord);
                    clear();
                    main_class.setScore(scoreCounter);
                }
            }
        });

        return view;
    }

    public interface ScoreListener {
        void setScore(int score);
    }

    public void clear() {
        LinearLayout buttonRow;
        Button btn;
        previousButton = null;
        answer = "";
        tvWord.setText(answer);
        for (int i = 0; i < gridButtons.getChildCount(); i++) {
            buttonRow = (LinearLayout) gridButtons.getChildAt(i);
            for (int j = 0; j < buttonRow.getChildCount(); j++) {
                btn = (Button) buttonRow.getChildAt(j);
                btn.setEnabled(true);
            }
        }
    }

    public boolean isNeighbor(Button button) {
        int prevInt = Integer.parseInt(previousButton.getTag().toString().substring(6));
        int prevRow = prevInt % 4 == 0 ? prevInt / 4 - 1 : prevInt / 4;
        int prevColumn = (prevInt - 1) % 4;

        int nextInt = Integer.parseInt(button.getTag().toString().substring(6));
        int nextRow = nextInt % 4 == 0 ? nextInt / 4 - 1 : nextInt / 4;
        int nextColumn = (nextInt - 1) % 4;

        if ((abs(nextRow - prevRow) <= 1 && abs(nextColumn - prevColumn) <= 1)) {
            return true;
        } else return false;

    }

    private boolean validWord(String str) {

        if (str.isEmpty()) {
            Log.i("TAG", "Empty string!");
        } else if (wordsSet.contains(str)) {
            return true;
        }
        return false;
    }

    // Iterates through button grid and sets each to a random character
    private void generateLetterGrid() {
        LinearLayout buttonRow;
        Button btn;
        for (int i = 0; i < gridButtons.getChildCount(); i++) {
            buttonRow = (LinearLayout) gridButtons.getChildAt(i);
            for (int j = 0; j < buttonRow.getChildCount(); j++) {
                btn = (Button) buttonRow.getChildAt(j);
                String temp = String.valueOf(Character.toChars((int) ('A' + (Math.random() * 26))));
                btn.setText(temp);
                btn.setOnClickListener(getOnClick(btn));
            }
        }
    }

    // Returns an onClickListener given a button; easy to use for boggle grid
    View.OnClickListener getOnClick(final Button button) {
        return new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(View v) {
                // We need to check for previous button for neighbor checking
                if (previousButton == null) {
                    answer = String.valueOf(Character.toLowerCase(button.getText().charAt(0)));
                    tvWord.setText(answer);
                    previousButton = button;
                    button.setEnabled(false);
                } else if (isNeighbor(button)) {
                    answer += String.valueOf(Character.toLowerCase(button.getText().charAt(0)));
                    tvWord.setText(answer);
                    previousButton = button;
                    button.setEnabled(false);
                } else {
                    Toast.makeText(getContext(), "Please select neighbor of previous letter", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void wordScore(String word) {
        // This method computes the entire score of the word based on the rules provided on the worksheet
        // Utilized the dictionary txt file to check if the word is a valid word
        int total_score = 0;                     // Final score of the word
        int vowelCounter = 0;                   // Number of vowels in word
        boolean doubleCharExists = false;       // Is there a double character in word?
        if (!(validWord(word)))                 // Checks if word is in dictionary
            total_score = -10;
        else {
            for (int i = 0; i < word.length(); i++) {
                char letter = word.charAt(i);
                if (vowels.indexOf(letter) != -1) {              // letter is a vowel
                    vowelCounter++;
                    total_score += 5;
                } else if (doublescore.indexOf(letter) != -1) {    // letter is a double score character
                    doubleCharExists = true;
                    total_score += 1;
                } else
                    total_score++;
            }
            if (vowelCounter < 2 || word.length() < 4) {                          // There must be at least 2 vowels for the word to be valid
                total_score = -10;
            } else if (doubleCharExists)                      // A double letter is in the word, apply the double multiplier
                total_score *= 2;

            // We remove the word once submitted
            wordsSet.remove(word);
        }
        if (total_score > 0) {
            Toast.makeText(getContext(), String.format("That's correct, +%d", total_score), Toast.LENGTH_LONG).show();    // Toast messages for scores
        } else {
            Toast.makeText(getContext(), String.format("That's incorrect, %d", total_score), Toast.LENGTH_LONG).show();
        }

        scoreCounter += total_score;
    }

    // This needs to regenerate the dictionary because we remove words that have already been chosen
    public void restartGame() {
        generateLetterGrid();
        generateDictionary();
        clear();
        scoreCounter = 0;
        main_class.setScore(scoreCounter);
    }


}
