package com.example.w5_p3;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class BottomFragment extends Fragment {
    private TextView tvScore;
    private Button newGame;
    private RestartListener main_class;

    public BottomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        main_class = (RestartListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom, container, false);

        tvScore = (TextView) view.findViewById(R.id.tvScore);
        newGame = (Button) view.findViewById(R.id.btnNewgame);

        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });

        return view;
    }

    public void restartGame() {
        main_class.restartGame();
    }

    public void setScore(int scoreCount) {
        tvScore.setText(String.format("Score: %d", scoreCount));
    }

    public interface RestartListener {
        void restartGame();
    }


}
