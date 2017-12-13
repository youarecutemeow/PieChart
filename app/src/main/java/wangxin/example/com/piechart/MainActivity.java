package wangxin.example.com.piechart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import wangxin.example.com.piechartlib.PieChart;
import wangxin.example.com.piechartlib.Piece;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private PieChart mPieChart;
    private Boolean isShowText = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPieChart = (PieChart)findViewById(R.id.piechart);
        mPieChart.setOnAddedPiece(new PieChart.OnAddedPiece() {
            @Override
            public void addedPie() {
                Toast.makeText(getBaseContext(),"you added a piece",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void click(View view){
        mPieChart.addPiece(new Piece(4,"其他"));
    }
    public void showText(View view){
        if (isShowText){
            mPieChart.setShowAllText(isShowText);
            isShowText = false;
        }
        else{
            mPieChart.setShowAllText(isShowText);
            isShowText = true;
        }


    }
}
