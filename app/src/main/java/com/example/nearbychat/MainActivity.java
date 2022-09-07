package com.example.nearbychat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;

import java.nio.charset.StandardCharsets;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private final Strategy star = Strategy.P2P_CLUSTER;
    private final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;
    ImageButton send,contacts;
    TextView status, tvmsgrecieve,uit,tvmsgsend;
    TextInputEditText etmsg,etcontact;
    ConnectionsClient connectionsClient;
    String token;
    String opponent;
    PayloadCallback payloadCallbackNext = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
            String msg = new String(payload.asBytes(), StandardCharsets.UTF_8);
            receiveMessage(msg);
        }
        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

        }
    };
    EndpointDiscoveryCallback endpointDiscoveryCallbackNext = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String s, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            connectionsClient.requestConnection(token, s, connectionLifecycleCallbackNext);
        }

        @Override
        public void onEndpointLost(@NonNull String s) {
            Toast.makeText(MainActivity.this, "Connection Lost", Toast.LENGTH_SHORT).show();
            status.setText("Status: Disconnected :(");
            connectionsClient.stopDiscovery();
            connectionsClient.stopAdvertising();
            connectionsClient.stopAllEndpoints();
            startDiscoveryNext();
            startAdvertisingNext();

        }
    };
    ConnectionLifecycleCallback connectionLifecycleCallbackNext = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
            connectionsClient.acceptConnection(s, payloadCallbackNext);
            opponent=s;
            Toast.makeText(MainActivity.this, "Authentication Digit: " + connectionInfo.getAuthenticationDigits(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {
            if (connectionResolution.getStatus().isSuccess()) {
                connectionsClient.stopAdvertising();
                connectionsClient.stopDiscovery();
                startAdvertisingPrevious();
                startDiscoveryPrevious();
                Toast.makeText(MainActivity.this, "Connection made :)", Toast.LENGTH_SHORT).show();
                status.setText("Status: Half Connection made :)");
            }
        }

        @Override
        public void onDisconnected(@NonNull String s) {
            Toast.makeText(MainActivity.this, "Disconnected :(", Toast.LENGTH_SHORT).show();
            status.setText("Status: Disconnected :(");
            connectionsClient.stopDiscovery();
            connectionsClient.stopAdvertising();
            connectionsClient.stopAllEndpoints();
            startDiscoveryNext();
            startAdvertisingNext();

        }
    };



    PayloadCallback payloadCallbackPrevious = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
            String msg = new String(payload.asBytes(), StandardCharsets.UTF_8);
            receiveMessage(msg);
        }
        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

        }
    };
    ConnectionLifecycleCallback connectionLifecycleCallbackPrevious = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
            connectionsClient.acceptConnection(s, payloadCallbackPrevious);
            Toast.makeText(MainActivity.this, "Authentication Digit: " + connectionInfo.getAuthenticationDigits(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {
            if (connectionResolution.getStatus().isSuccess()) {
                connectionsClient.stopAdvertising();
                connectionsClient.stopDiscovery();
                opponent = s;
                Toast.makeText(MainActivity.this, "Connection made :)", Toast.LENGTH_SHORT).show();
                status.setText("Status: Connection made :)");
            }
        }

        @Override
        public void onDisconnected(@NonNull String s) {
            Toast.makeText(MainActivity.this, "Disconnected :(", Toast.LENGTH_SHORT).show();
            status.setText("Status: Disconnected :(");
            connectionsClient.stopDiscovery();
            connectionsClient.stopAdvertising();
            connectionsClient.stopAllEndpoints();
            startDiscoveryNext();
            startAdvertisingNext();

        }
    };
    EndpointDiscoveryCallback endpointDiscoveryCallbackPrevious = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String s, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            connectionsClient.requestConnection(token, s, connectionLifecycleCallbackPrevious);
        }

        @Override
        public void onEndpointLost(@NonNull String s) {
            Toast.makeText(MainActivity.this, "Connection Lost", Toast.LENGTH_SHORT).show();
            status.setText("Status: Disconnected :(");
            connectionsClient.stopDiscovery();
            connectionsClient.stopAdvertising();
            connectionsClient.stopAllEndpoints();
            startDiscoveryNext();
            startAdvertisingNext();

        }
    };


    static String getAlphaNumericString(int n) {
        int lowerLimit = 97;
        int upperLimit = 122;
        Random random = new Random();
        StringBuilder r = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int nextRandomChar = lowerLimit
                    + (int) (random.nextFloat()
                    * (upperLimit - lowerLimit + 1));
            r.append((char) nextRandomChar);
        }
        return r.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String copiedUit = getIntent().getStringExtra("UIT");
        send = findViewById(R.id.send);
        etmsg = findViewById(R.id.etmsg);
        tvmsgrecieve = findViewById(R.id.tvmsgrecieve);
        tvmsgsend=findViewById(R.id.tvmsgsent);

        status = findViewById(R.id.status);
        uit=findViewById(R.id.uit);
        contacts=findViewById(R.id.contact);
        etcontact=findViewById(R.id.etcontact);
        etcontact.setText(copiedUit);

        SharedPreferences sharedPref = getSharedPreferences("name", Context.MODE_PRIVATE);
        if (sharedPref.getString("Token", "").equals("")) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("Token", getAlphaNumericString(10));
            editor.apply();
        }
        token = sharedPref.getString("Token", "");
        uit.setText("UIT : "+token);

        Log.i("Token", token);

        connectionsClient = Nearby.getConnectionsClient(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestBluetooth();
        } else {
            requestPermissions();
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etmsg.getText().toString().equals(""))
                    sendMessage(etmsg.getText().toString(),etcontact.getText().toString(),token);
            }
        });
        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ContactActivity.class));
                finish();
            }
        });
    }

    public void sendMessage(String msg , String receiver ,String sender) {
        if(receiver.equals("")||receiver.length()<10||receiver.length()>10)
        {
            Toast.makeText(MainActivity.this, "Please enter correct contact", Toast.LENGTH_SHORT).show();
        } else
            {
            final String secretKey = "com.example.nearbychat";
            String encryptedMessage = AES.encrypt(msg, secretKey);
            String finalMessage = encryptedMessage +sender+ receiver;
            connectionsClient.sendPayload(opponent, Payload.fromBytes(finalMessage.getBytes(StandardCharsets.UTF_8)));
            etmsg.setText("");
                tvmsgsend.setText("You :"+ msg);
                tvmsgsend.requestFocus();

            }
    }
    public void receiveMessage(String msg){
//        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        String message=msg.substring(0,msg.length()-20);
        String receiver=msg.substring(msg.length()-10);
        String sender=msg.substring(msg.length()-20,msg.length()-10);
        if(receiver.equals(token)) {
            final String secretKey = "com.example.nearbychat";
            String decryptedString = AES.decrypt(message, secretKey);
            tvmsgrecieve.setText(sender + " : " + decryptedString);
            tvmsgrecieve.requestFocus();
        }
        else{
            if(!sender.equals(token))
            sendMessage(msg,receiver,sender);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startAdvertisingNext();
                    startDiscoveryNext();
                } else {
                    Toast.makeText(MainActivity.this, "Please grant these permission to use the service", Toast.LENGTH_SHORT).show();
                }
            case 1:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(MainActivity.this, "Please grant these permission to use the service", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void startAdvertisingNext() {
        AdvertisingOptions options = new AdvertisingOptions.Builder().setStrategy(star).build();
        connectionsClient.startAdvertising((token), "ritik", connectionLifecycleCallbackNext, options).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MainActivity.this, "Advertising", Toast.LENGTH_SHORT).show();
                status.setText("Status: Advertising");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("AdvertisingFailure", "Error requesting connection", e);
                status.setText("Status: " + e);
//                Toast.makeText(MainActivity.this, "Make sure WIFI , BLUETOOTH and LOCATION are ON", Toast.LENGTH_SHORT).show();
                startAdvertisingNext();
            }
        });
    }

    private void startDiscoveryNext() {
        DiscoveryOptions options = new DiscoveryOptions.Builder().setStrategy(star).build();
        connectionsClient.startDiscovery("ritik", endpointDiscoveryCallbackNext, options).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MainActivity.this, "Discovering", Toast.LENGTH_SHORT).show();

                status.setText("Status: Discovering");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("DiscoveryFailure", "Error requesting connection", e);
                status.setText("Status: " + e);

//                Toast.makeText(MainActivity.this, "Make sure WIFI , BLUETOOTH and LOCATION are ON", Toast.LENGTH_SHORT).show();
                startDiscoveryNext();

            }
        });
    }
    private void startAdvertisingPrevious() {
        AdvertisingOptions options = new AdvertisingOptions.Builder().setStrategy(star).build();
        connectionsClient.startAdvertising((token), "ritik", connectionLifecycleCallbackPrevious, options).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MainActivity.this, "Advertising", Toast.LENGTH_SHORT).show();
                status.setText("Status: Advertising again");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("AdvertisingFailure", "Error requesting connection", e);
                status.setText("Status: " + e);
//                Toast.makeText(MainActivity.this, "Make sure WIFI , BLUETOOTH and LOCATION are ON", Toast.LENGTH_SHORT).show();
                startAdvertisingPrevious();
            }
        });
    }
    private void startDiscoveryPrevious() {
        DiscoveryOptions options = new DiscoveryOptions.Builder().setStrategy(star).build();
        connectionsClient.startDiscovery("ritik", endpointDiscoveryCallbackPrevious, options).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MainActivity.this, "Discovering", Toast.LENGTH_SHORT).show();

                status.setText("Status: Discovering again");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("DiscoveryFailure", "Error requesting connection", e);
                status.setText("Status: " + e);

//                Toast.makeText(MainActivity.this, "Make sure WIFI , BLUETOOTH and LOCATION are ON", Toast.LENGTH_SHORT).show();
                startDiscoveryNext();

            }
        });
    }

    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            startAdvertisingNext();
            startDiscoveryNext();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public void requestBluetooth() {
        if (ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.BLUETOOTH_ADVERTISE) ==
                PackageManager.PERMISSION_GRANTED) {
            startAdvertisingNext();
            startDiscoveryNext();
        } else {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE},
                    0);
        }
    }

    @Override
    protected void onStop() {
        connectionsClient.stopDiscovery();
        connectionsClient.stopAdvertising();
        connectionsClient.stopAllEndpoints();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        connectionsClient.stopDiscovery();
        connectionsClient.stopAdvertising();
        connectionsClient.stopAllEndpoints();
        super.onDestroy();
    }
}


