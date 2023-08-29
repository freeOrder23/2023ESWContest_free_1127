package com.example.connectbluetooth;

import static kotlinx.coroutines.DelayKt.delay;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

//--
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
//--

//--

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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//--
public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    TextView textStatus;
    Button btnParied;
    ListView listView;

    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> pairedDevices;
    ArrayAdapter<String> btArrayAdapter;
    ArrayList<String> deviceAddressArray;


    private final static int REQUEST_ENABLE_BT = 1;
    BluetoothSocket btSocket = null;
    private static final int SINGLE_PERMISSION = 1004;
    com.minseon.bluetooth_example.ConnectedThread connectedThread;

    //--
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;
    private InputStream mInputStream;
    private TextView receivedDataTextView;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // Standard SerialPortService ID

    //--

    private DatabaseReference database;
    private ListView parkingLotListView;
    private List<ParkingLot> parkingLotList;


    private boolean hexEnabled = false;
    private String newline = TextUtil.newline_crlf;
    private boolean pendingNewline = false;
    private TextView receiveText;

    private String receivedData;

    private String selectedDeviceAddress;








    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        database = FirebaseDatabase.getInstance().getReference();





        // Get permission
        String[] permission_list = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(MainActivity.this, permission_list, 1);

        // Enable bluetooth
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, SINGLE_PERMISSION);
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // variables
        textStatus = (TextView) findViewById(R.id.text_status);
        btnParied = (Button) findViewById(R.id.btn_paired);

        listView = (ListView) findViewById(R.id.listview);




        // Show paired devices
        btArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        deviceAddressArray = new ArrayList<>();
        listView.setAdapter(btArrayAdapter);

        listView.setOnItemClickListener(new myOnItemClickListener());












    }

    public void OnclikButtonParkingLotlist(View view) {
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);

    }



    public void onClickButtonPaired(View view) {
        btArrayAdapter.clear();
        if (deviceAddressArray != null && !deviceAddressArray.isEmpty()) {
            deviceAddressArray.clear();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, SINGLE_PERMISSION);
            return;
        }
        pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                btArrayAdapter.add(deviceName);
                deviceAddressArray.add(deviceHardwareAddress);

            }
        }
    }

    public class myOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getApplicationContext(), btArrayAdapter.getItem(position), Toast.LENGTH_SHORT).show();

            textStatus.setText("try...");

            final String name = btArrayAdapter.getItem(position); // get name
            final String address = deviceAddressArray.get(position); // get address
            boolean flag = true;

            BluetoothDevice device = btAdapter.getRemoteDevice(address);

            // create & connect socket
            try {
                btSocket = createBluetoothSocket(device);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, SINGLE_PERMISSION);
                    return;
                }
                btSocket.connect();
                btArrayAdapter.clear();


            } catch (IOException e) {
                flag = false;
                textStatus.setTextColor(getResources().getColor(R.color.colorconnectfail));
                textStatus.setText("connection failed!");
                e.printStackTrace();
            }

            // start bluetooth communication
            if (flag) {
                textStatus.setTextColor(getResources().getColor(R.color.colorRecieveText));
                textStatus.setText("connected to " + name);
                connectedThread = new com.minseon.bluetooth_example.ConnectedThread(btSocket);
                connectedThread.start();
                try {
                    mInputStream = btSocket.getInputStream(); // InputStream 초기화 추가
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }


            if (flag) {//연결되면 a를 보내는
                if (connectedThread != null) {
                    connectedThread.write("a");
                    selectedDeviceAddress = address;

                    SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor = prefs.edit();
                    prefsEditor.putString("selectedDeviceAddress", selectedDeviceAddress);
                    prefsEditor.apply();



                }
            }

            if (flag) {
            startListeningForData();
            }


        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection", e);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, SINGLE_PERMISSION);

        }
        return device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }

    //-

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == 0) {
                String receivedData = (String) message.obj;
                //receivedDataTextView.setText(receivedData);
            }
            return true;
        }
    });


    //블루투스 데이터를 받아오는 부분
    private void startListeningForData() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                int bytesRead;

                while (true) {
                    try {
                        bytesRead = mInputStream.read(buffer);
                        String receivedData = new String(buffer, 0, bytesRead);
                        Message message = mHandler.obtainMessage(0, receivedData);
                        mHandler.sendMessage(message);


                        // 출력된 데이터를 로그로 확인
                        Log.d(TAG, "Received data: " + receivedData);

                        //== leave 반응
                        // Leave 데이터를 수신하면 해당 주차장의 capacity 값을 +1하여 업데이트
                        if (receivedData.startsWith("Leave ")) {
                            String parkingLotName = receivedData.substring(6); //주차당 이름
                            String path = "parking_lots"; // 최상위 경로

                            DatabaseReference parkingLotReference = database.child(path);
                            Query query = parkingLotReference.orderByChild("name").equalTo(parkingLotName);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot parkingLotSnapshot : dataSnapshot.getChildren()) {
                                        String parkingLotId = parkingLotSnapshot.getKey();
                                        String newPath = path + "/" + parkingLotId;

                                        Integer capacity = parkingLotSnapshot.child("capacity").getValue(Integer.class);
                                        int newCapacity = 0;
                                        if (capacity != null) {
                                            newCapacity = capacity + 1; // Leave일 경우 capacity +1
                                            parkingLotSnapshot.child("capacity").getRef().setValue(newCapacity);
                                        }

                                        Log.d(TAG, "Capacity of " + parkingLotName + " updated to " + newCapacity);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // 에러 처리
                                }
                            });
                        }
                        //== leave 반응

                        //== come 반응
                        if (receivedData.startsWith("Come ")) {
                            String parkingLotName = receivedData.trim().substring(6); //주차당 이름

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "\""+parkingLotName +"\" 주차장 사용이 확인 되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            String path = "parking_lots"; // 최상위 경로

                            DatabaseReference parkingLotReference = database.child(path);
                            Query query = parkingLotReference.orderByChild("name").equalTo(parkingLotName);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot parkingLotSnapshot : dataSnapshot.getChildren()) {
                                        String parkingLotId = parkingLotSnapshot.getKey();
                                        String newPath = path + "/" + parkingLotId;

                                        Integer capacity = parkingLotSnapshot.child("capacity").getValue(Integer.class);
                                        int newCapacity = 0;
                                        if (capacity != null) {
                                            newCapacity = capacity - 1; // Leave일 경우 capacity +1
                                            parkingLotSnapshot.child("capacity").getRef().setValue(newCapacity);
                                        }

                                        Log.d(TAG, "Capacity of " + parkingLotName + " updated to " + newCapacity);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // 에러 처리
                                }
                            });
                        }
                        //== come 반응



                        // UI 업데이트를 위해 UI 쓰레드에서 처리

                        //--
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        thread.start();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        //블루투스 소켓, 스트림 해제
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            if (mInputStream != null) {
                mInputStream.close();
            }
            if (mBluetoothSocket != null) {
                mBluetoothSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}




