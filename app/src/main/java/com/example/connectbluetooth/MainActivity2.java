package com.example.connectbluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//주차장을 추가하고 추가된 주차장들을 보여주는 액티비티
public class MainActivity2 extends AppCompatActivity {

    private DatabaseReference database;
    private ListView parkingLotListView;
    private List<ParkingLot> parkingLotList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        FirebaseApp.initializeApp(this);

        database = FirebaseDatabase.getInstance().getReference();
        parkingLotListView = findViewById(R.id.parkingLotListView);
        parkingLotList = new ArrayList<>();
        Button addButton = findViewById(R.id.addButton);

        //Button deleteButton = findViewById(R.id.deleteButton);
        final EditText nameEditText = findViewById(R.id.nameEditText);
        final EditText capacityEditText = findViewById(R.id.capacityEditText);

        // Button updateButton = findViewById(R.id.updateButton);



        final ArrayAdapter<ParkingLot> adapter = new ArrayAdapter<>(
                this, R.layout.list_item_parking_lot, R.id.nameTextView, parkingLotList);
        parkingLotListView.setAdapter(adapter);

        database.child("parking_lots").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                parkingLotList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ParkingLot parkingLot = snapshot.getValue(ParkingLot.class);
                    parkingLotList.add(parkingLot);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 에러 처리
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = String.valueOf(nameEditText.getText()); // 사용자가 입력한 이름
                int capacity = Integer.parseInt(capacityEditText.getText().toString());  // 입력된 텍스트를 숫자로 변환 // 사용자가 입력한 숫자

                String parkingLotId = database.child("parking_lots").push().getKey();
                ParkingLot parkingLot = new ParkingLot(name, capacity, "예약없음");
                if (parkingLotId != null) {
                    database.child("parking_lots").child(parkingLotId).setValue(parkingLot);
                }
            }
        });


        //== 목록 클릭 예약 기능



        //== 목록 클릭 예약 기능


    }
}





class ParkingLot {
    private String name;
    private int capacity;
    private String book;

    public ParkingLot() {
        // Default constructor required for Firebase
    }

    public ParkingLot(String name, int capacity, String book) {
        this.name = name;
        this.capacity = capacity;
        this.book = book;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    @Override
    public String toString() {
        return name + "주차장 "+ "\n"+"장애인전용주차가능대수:  " + capacity  ;
    }
}

