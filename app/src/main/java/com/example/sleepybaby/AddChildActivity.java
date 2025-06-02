package com.example.sleepybaby;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class AddChildActivity extends AppCompatActivity {
    private static final String TAG = "AddChildActivity";
    
    private TextInputEditText editTextName;
    private TextInputEditText editTextBirthDate;
    private MaterialButton buttonGenderMale;
    private MaterialButton buttonGenderFemale;
    private MaterialButton buttonSaveChild;
    private ImageButton buttonBack;
    private ImageView imageViewPhoto;
    private MaterialButton buttonSelectPhoto;
    private String selectedGender = "";
    private String selectedPhotoUri = null;
    private static final int REQUEST_SELECT_PHOTO = 101;
    
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
        imageViewPhoto = findViewById(R.id.imageViewPhoto);
        buttonSelectPhoto = findViewById(R.id.buttonSelectPhoto);
        
        databaseHelper = new DatabaseHelper(this);
        selectedDate = Calendar.getInstance();
        birthDate = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Log.d(TAG, "Views initialized successfully");

        // Doğum tarihi seçici
        editTextBirthDate.setOnClickListener(v -> showDatePicker());
        buttonSelectPhoto.setOnClickListener(v -> openGallery());
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
                    0,  // Varsayılan uyanma dakikası
                    selectedPhotoUri // Fotoğraf URI
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

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_PHOTO && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                try {
                    // Fotoğrafı uygulamanın cache dizinine kopyala
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    File file = new File(getCacheDir(), "child_photo_" + System.currentTimeMillis() + ".jpg");
                    OutputStream outputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    inputStream.close();
                    outputStream.close();
                    selectedPhotoUri = Uri.fromFile(file).toString();
                    imageViewPhoto.setImageURI(Uri.fromFile(file));
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Fotoğraf yüklenemedi", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
