package com.example.sleepybaby;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddChildActivity extends AppCompatActivity {
    private static final String TAG = "AddChildActivity";

    private TextInputEditText editTextName;
    private TextInputEditText editTextBirthDate;
    private MaterialButton buttonGenderMale;
    private MaterialButton buttonGenderFemale;
    private MaterialButton buttonSaveChild;
    private ImageButton buttonBack;
    
    private String selectedGender = "";
    
    private DatabaseHelper databaseHelper;
    private Calendar selectedDate;
    private Calendar birthDate;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        try {
            Log.d(TAG, "Initializing views...");
            initializeViews();
            setupGenderButtons();
            setupSaveButton();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Uygulama başlatılırken hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    private void initializeViews() {
        // Toolbar'ı ayarla
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Geri tuşu
        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());

        editTextName = findViewById(R.id.editTextName);
        editTextBirthDate = findViewById(R.id.editTextBirthDate);
        buttonGenderMale = findViewById(R.id.buttonGenderMale);
        buttonGenderFemale = findViewById(R.id.buttonGenderFemale);
        buttonSaveChild = findViewById(R.id.buttonSaveChild);
        
        databaseHelper = new DatabaseHelper(this);
        selectedDate = Calendar.getInstance();
        birthDate = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Log.d(TAG, "Views initialized successfully");

        // Doğum tarihi seçici
        editTextBirthDate.setOnClickListener(v -> showDatePicker());
    }
    
    private void setupGenderButtons() {
        buttonGenderMale.setOnClickListener(v -> {
            selectedGender = "Erkek";
            buttonGenderMale.setStrokeColor(getColorStateList(R.color.primary));
            buttonGenderMale.setTextColor(getColor(R.color.primary));
            buttonGenderFemale.setStrokeColor(getColorStateList(R.color.gray));
            buttonGenderFemale.setTextColor(getColor(R.color.gray));
        });

        buttonGenderFemale.setOnClickListener(v -> {
            selectedGender = "Kız";
            buttonGenderFemale.setStrokeColor(getColorStateList(R.color.primary));
            buttonGenderFemale.setTextColor(getColor(R.color.primary));
            buttonGenderMale.setStrokeColor(getColorStateList(R.color.gray));
            buttonGenderMale.setTextColor(getColor(R.color.gray));
        });
    }
    
    private void setupSaveButton() {
        buttonSaveChild.setOnClickListener(v -> {
            try {
                Log.d(TAG, "Save button clicked");
                
                // İsim kontrolü
                String name = editTextName.getText().toString().trim();
                if (name.isEmpty()) {
                    editTextName.setError("İsim gerekli");
                    editTextName.requestFocus();
                    return;
                }

                // Doğum tarihi
                String birthDateStr = editTextBirthDate.getText().toString().trim();
                if (birthDateStr.isEmpty()) {
                    Toast.makeText(this, "Doğum tarihi gerekli", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Cinsiyet kontrolü
                if (selectedGender.isEmpty()) {
                    Toast.makeText(this, "Lütfen cinsiyet seçiniz", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d(TAG, "Input values - Name: " + name + ", BirthDate: " + birthDateStr + 
                      ", Gender: " + selectedGender);
                
                // String tarihi Date'e çevir
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date birthDate = sdf.parse(birthDateStr);
                
                // Veritabanına kaydet
                Log.d(TAG, "Attempting to add child to database...");
                long birthDateMillis = birthDate.getTime();
                boolean inserted = databaseHelper.addChild(
                    name,
                    birthDateMillis,
                    selectedGender,
                    21, // Varsayılan uyku saati
                    0,  // Varsayılan uyku dakikası
                    7,  // Varsayılan uyanma saati
                    0   // Varsayılan uyanma dakikası
                );
                
                if (inserted) {
                    Log.d(TAG, "Child added successfully");
                    Toast.makeText(this, "Çocuk başarıyla eklendi!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e(TAG, "Failed to add child");
                    Toast.makeText(this, "Ekleme sırasında hata oluştu.", Toast.LENGTH_SHORT).show();
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error in save button click: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(this, "Bir hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                birthDate.set(year, month, dayOfMonth);
                editTextBirthDate.setText(dateFormat.format(birthDate.getTime()));
            },
            birthDate.get(Calendar.YEAR),
            birthDate.get(Calendar.MONTH),
            birthDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}
