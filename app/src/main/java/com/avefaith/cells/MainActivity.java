package com.avefaith.cells;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    static String TAG = "MainActivity";
    int randomHeight, randomWidth;
    Button[][] cells;
    TextView mines;
    final int MINESCONST = 5;
    int FlagsCurrent = MINESCONST;
    int MinesCurrent = MINESCONST;
    final int WIDTH = 6;
    final int HEIGHT = 6;
    int [][] MinesOnBoard = new int[HEIGHT][WIDTH];
    int [][] IndexesOfMines = new int[HEIGHT][WIDTH];
    int [][] HiddenBoard = new int[HEIGHT][WIDTH];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generate();
        mines = findViewById(R.id.Mines);
        mines.setText("" + MINESCONST + "/" + FlagsCurrent);
    }


    public void open_mines() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++){
                if(MinesOnBoard[i][j] == 1){
                    cells[i][j].setBackgroundColor(Color.RED);
                    cells[i][j].setText("BOMB");
                }
            }
        }
    }

    public void open_all() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++){
                if(MinesOnBoard[i][j] != 1){
                    cells[i][j].setBackgroundColor(Color.GREEN);
                    cells[i][j].setText("" + IndexesOfMines[i][j]);
                }
            }
        }
    }


    public void open(int column, int row) {
        cells[column][row].setBackgroundColor(Color.GREEN);
        cells[column][row].setText("" + IndexesOfMines[column][row]);
        HiddenBoard[column][row] = 1;
        if (IndexesOfMines[column][row] == 0) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if ((column + i) < WIDTH && (column + i) > -1 && (row + j) < HEIGHT && (row + j) > -1 &&
                            !(i == 0 && j == 0)) {
                        if (HiddenBoard[column + i][row + j] == 0) {
                            open(column + i, row + j);
                        }
                    }
                }
            }
        }
    }


    public void generate() {
        GridLayout layout = findViewById(R.id.GRID);
        layout.removeAllViews();
        layout.setColumnCount(WIDTH);
        cells = new Button[HEIGHT][WIDTH];

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        //Генерация мин на поле
        for(int k = 0; k < MINESCONST; k ++){
            randomHeight = (int)(Math.random()*HEIGHT);
            randomWidth = (int)(Math.random()*WIDTH);
            while(MinesOnBoard[randomHeight][randomWidth] == 1){
                randomHeight = (int)(Math.random()*HEIGHT);
                randomWidth = (int)(Math.random()*WIDTH);
            }
            MinesOnBoard[randomHeight][randomWidth] = 1;
        }

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                cells[i][j] = (Button) inflater.inflate(R.layout.cell, layout, false);
                HiddenBoard[i][j] = 0;
            }
        }


        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                cells[i][j].setBackgroundColor(Color.LTGRAY);
                // Заполнение ячеек количеством мин вокруг
                int SummOfMines = 0;
                for(int t = i - 1; t <= i + 1; t ++){
                    for(int k = j - 1; k <= j + 1; k ++){
                        if(t >= 0 && k >= 0 && t <= HEIGHT - 1 && k <= WIDTH - 1 ){
                            if(MinesOnBoard[t][k] == 1) {
                                SummOfMines += 1;
                            }
                        }
                    }
                }
                if(MinesOnBoard[i][j] == 1){
                    SummOfMines --;
                }
                IndexesOfMines[i][j] = SummOfMines;
            }
        }

        boolean[] game = {true};

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                int finalI = i;
                int finalJ = j;


                cells[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ColorDrawable viewColor = (ColorDrawable) view.getBackground();
                        int colorId = viewColor.getColor();
                        if(game[0] && colorId != Color.BLUE && colorId != Color.RED) {
                            if (MinesOnBoard[finalI][finalJ] == 0 && HiddenBoard[finalI][finalJ] == 0) {
                                open(finalI, finalJ);
                            } else if (MinesOnBoard[finalI][finalJ] == 1 && HiddenBoard[finalI][finalJ] == 0){
                                view.setBackgroundColor(Color.RED);
                                HiddenBoard[finalI][finalJ] = 1;
                                game[0] = false;
                                open_mines();
                                Toast.makeText(getApplicationContext(), "YOU LOST", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                cells[i][j].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ColorDrawable viewColor = (ColorDrawable) v.getBackground();
                        int colorId = viewColor.getColor();
                        // На поле поставлен флаг
                        if(colorId == Color.BLUE && game[0]){
                            v.setBackgroundColor(Color.LTGRAY);
                            FlagsCurrent ++;
                            cells[finalI][finalJ].setText(".");
                            if(MinesOnBoard[finalI][finalJ] == 1){
                                MinesCurrent ++;
                            }
                        }
                        //
                        else if(colorId != Color.RED && HiddenBoard[finalI][finalJ] == 0 && game[0]){
                            if(FlagsCurrent <= 0){
                                Toast.makeText(getApplicationContext(), "Out of flags", Toast.LENGTH_LONG).show();
                            }
                            else {
                                v.setBackgroundColor(Color.BLUE);
                                cells[finalI][finalJ].setText("FLAG");
                                FlagsCurrent--;
                                if (MinesOnBoard[finalI][finalJ] == 1) {
                                    MinesCurrent--;
                                }
                            }
                        }
                        mines.setText(""+MINESCONST+"/"+FlagsCurrent);
                        if(MinesCurrent == 0){
                            game[0] = false;
                            open_all();
                            Toast.makeText(getApplicationContext(), "WIN!!!!", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    }
                });
//                if(MinesOnBoard[i][j] == 0) {
//                    cells[i][j].setText("" + (IndexesOfMines[i][j]));
//                    cells[i][j].setTag("" + (IndexesOfMines[i][j]));
//                }
//                else{
//                    cells[i][j].setText("M");
//                    cells[i][j].setTag("M");
//                }
                cells[i][j].setTag(".");
                cells[i][j].setText(".");
                layout.addView(cells[i][j]);
            }
        }

    }


}