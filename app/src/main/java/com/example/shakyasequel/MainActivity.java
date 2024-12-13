package com.example.shakyasequel;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.shakyasequel.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SQLiteDatabase database;
    private ArrayAdapter<String> adapter;
    private final ArrayList<String> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        binding.listBooks.setAdapter(adapter);

        database = new DBHelper(this).getWritableDatabase();

        binding.btnInsert.setOnClickListener(view -> {
            if (binding.edBook.length() == 0 || binding.edPrice.length() == 0) {
                Toast.makeText(MainActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            database.execSQL(
                    "INSERT INTO books(name, price) VALUES (?, ?)", new Object[]{
                            binding.edBook.getText(),
                            binding.edPrice.getText()
                    }
            );

            Toast.makeText(MainActivity.this, "Book Inserted", Toast.LENGTH_SHORT).show();

            binding.edBook.setText("");
            binding.edPrice.setText("");
        });

        binding.btnUpdate.setOnClickListener(view -> {
            if (binding.edBook.length() == 0 || binding.edPrice.length() == 0) {
                Toast.makeText(MainActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            database.execSQL("UPDATE books SET price = ? WHERE name = ?", new Object[]{
                    binding.edPrice.getText(),
                    binding.edBook.getText()
            });

            Toast.makeText(MainActivity.this, "Book Updated", Toast.LENGTH_SHORT).show();

            binding.edBook.setText("");
            binding.edPrice.setText("");
        });

        binding.btnDelete.setOnClickListener(view -> {
            if (binding.edBook.length() == 0) {
                Toast.makeText(MainActivity.this, "Please fill in the book name", Toast.LENGTH_SHORT).show();
                return;
            }

            database.execSQL("DELETE FROM books WHERE name = ?", new Object[]{
                    binding.edBook.getText()
            });

            binding.edBook.setText("");
            binding.edPrice.setText("");
        });

        binding.btnQuery.setOnClickListener(view -> {
            Cursor cursor;
            if (binding.edBook.length() == 0) {
                cursor = database.rawQuery("SELECT * FROM books", null);
            } else {
                cursor = database.rawQuery("SELECT * FROM books WHERE name = ?", new String[]{
                        binding.edBook.getText().toString()
                });
            }

            int count = cursor.getCount();
            Toast.makeText(MainActivity.this, "There are " + count + " book" + (count == 1 ? "" : "s"), Toast.LENGTH_SHORT).show();

            items.clear();
            if (cursor.moveToNext()) {
                do {
                    items.add(cursor.getString(0) + ", " + cursor.getString(1));
                } while (cursor.moveToNext());
            }
            adapter.notifyDataSetChanged();

            binding.edBook.setText("");
            binding.edPrice.setText("");
            cursor.close();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }
}
