package com.example.pdfdoccreatorgeeks;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button generatePDFbtn;
    int pageHeight = 1330;
    int pageWidth = 792;

    Bitmap bmp, scaledbmp;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        generatePDFbtn = findViewById(R.id.idBtnGeneratePDF);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bookshelf);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);

        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }

        generatePDFbtn.setOnClickListener((View v)->{ generatePDF(); });

    }

    private void generatePDF() {
        // creating an object variable for our PDF document.
        PdfDocument pdfDocument = new PdfDocument();

        // two variables for paint "paint" is used for drawing shapes and we will use "title" for adding text in our PDF file.
        Paint paint = new Paint();
        Paint title = new Paint();

        // adding page info to our PDF file in which we will be passing our pageWidth, pageHeight and number of pages and after that we are calling it to create our PDF.
        PdfDocument .PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();

        // setting start page for our PDF file.
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        // creating a variable for canvas from our page of PDF.
        Canvas canvas = myPage.getCanvas();


        if (scaledbmp != null) {
            // drawing our image on our PDF file.
            canvas.drawBitmap(scaledbmp, 56, 40, paint);
        } else {
            Log.e("generatePDF", "Bitmap is null. Skipping bitmap drawing.");
        }

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(15);
        title.setColor(ContextCompat.getColor(this, R.color.black));

        Paint invoicePaint = new Paint();
        invoicePaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        invoicePaint.setTextSize(35);
        invoicePaint.setColor(Color.parseColor("#f2db07"));

        Paint labelPaint = new Paint();
        labelPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        labelPaint.setTextSize(15);

        Paint paintName = new Paint();
        paintName.setTextSize(20);        // Set text size
        paintName.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));


        Paint paintColumnTitle = new Paint();
        paintColumnTitle.setTextSize(20);
        paintColumnTitle.setColor(Color.parseColor("#002D62"));
        paintColumnTitle.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        int frX= 210;
        int frY = 100;
        canvas.drawText("Bookshelf",frX,frY,title);
        canvas.drawText("KOREGAON PARK.", frX, frY+20, title);
        canvas.drawText("PH-NO :  9090909090", frX, frY+40, title);

        String text = "INVOICE";
        float InTextWidth = invoicePaint.measureText(text);
        float xinv = (pageWidth - InTextWidth) / 2;
        float yinv = 220;
        canvas.drawText(text, xinv, yinv, invoicePaint);

        int cX = 60;
        canvas.drawText("Customer Name",cX,yinv+40,paintColumnTitle);
        canvas.drawText("Pranita",cX,yinv+60,title);
        canvas.drawText("Mobile Number",cX+400,yinv+40,paintColumnTitle);
        canvas.drawText("9090909090", cX+400, yinv+60, title);

        canvas.drawText("Invoice No:",cX,yinv+90,paintColumnTitle);
        canvas.drawText("00000000",cX,yinv+110,title);
        canvas.drawText("Date",cX+400,yinv+90,paintColumnTitle);
        canvas.drawText("24/09/2024", cX+400, yinv+110, title);

        int rightMargin = 100;

        int y1 = 380;  // y-coordinate remains the same (same line)
        int xItem = 105;   // Starting x position for "ITEM"
        int xQty = 350;    // x position for "QTY"
        int xRate = 450;  // x position for "RATE"
        int xTotal = 550; // x position for "TOTAL"

        Paint linePaint = new Paint();
        linePaint.setColor(android.graphics.Color.BLACK); // Set the color for the line
        linePaint.setStrokeWidth(2);
        float stopX = mypageInfo.getPageWidth() - 50;
        float startX = 50;

        // Drawing the table-likOa single line with proper spacing
        canvas.drawText("BOOK", xItem, y1, paintColumnTitle);
        canvas.drawText("QTY", xQty, y1, paintColumnTitle);
        canvas.drawText("RATE", xRate, y1, paintColumnTitle);
        canvas.drawText("TOTAL", xTotal, y1, paintColumnTitle);

        // Draw the horizontal line
        canvas.drawLine(startX, y1+20, stopX, y1+20, linePaint);

        Paint paintData = new Paint();
        paintData.setTextSize(20);        // Set text size
        paintData.setColor(Color.DKGRAY);    // Set text color
        paintData.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL)); // Set text style to italic

         y1 = y1 + 60;
        canvas.drawText("Alchemist", xItem, y1, paintData);
        canvas.drawText("1", xQty, y1, paintData);
        canvas.drawText("00.0", xRate, y1, paintData);
        canvas.drawText("00.0", xTotal, y1, paintData);
        canvas.drawLine(startX, y1+20, stopX, y1+20, linePaint);

        y1 = y1 + 60;
        canvas.drawText("11 Rules of life", xItem, y1, paintData);
        canvas.drawText("1", xQty, y1, paintData);
        canvas.drawText("00.0", xRate, y1, paintData);
        canvas.drawText("00.0", xTotal, y1, paintData);
        canvas.drawLine(startX, y1+20, stopX, y1+20, linePaint);

        y1 = y1 + 60;
        canvas.drawText("Harry Potter", xItem, y1, paintData);
        canvas.drawText("1", xQty, y1, paintData);
        canvas.drawText("00.0", xRate, y1, paintData);
        canvas.drawText("00.0", xTotal, y1, paintData);
        canvas.drawLine(startX, y1+20, stopX, y1+20, linePaint);

        y1 = y1 + 60;
        canvas.drawText("Sherlock holmes", xItem, y1, paintData);
        canvas.drawText("1", xQty, y1, paintData);
        canvas.drawText("00.0", xRate, y1, paintData);
        canvas.drawText("00.0", xTotal, y1, paintData);
        canvas.drawLine(startX, y1+20, stopX, y1+20, linePaint);

        y1 = y1 + 60;
        canvas.drawText("Deep Work", xItem, y1, paintData);
        canvas.drawText("1", xQty, y1, paintData);
        canvas.drawText("00.0", xRate, y1, paintData);
        canvas.drawText("00.0", xTotal, y1, paintData);
        canvas.drawLine(startX, y1+20, stopX, y1+20, linePaint);

        y1 = y1 + 60;
        canvas.drawText("5 am Club", xItem, y1, paintData);
        canvas.drawText("1", xQty, y1, paintData);
        canvas.drawText("00.0", xRate, y1, paintData);
        canvas.drawText("00.0", xTotal, y1, paintData);
        canvas.drawLine(startX, y1+20, stopX, y1+20, linePaint);

        y1 = y1 + 60;
        canvas.drawText("Secret", xItem, y1, paintData);
        canvas.drawText("1", xQty, y1, paintData);
        canvas.drawText("00.0", xRate, y1, paintData);
        canvas.drawText("00.0", xTotal, y1, paintData);
        canvas.drawLine(startX, y1+20, stopX, y1+20, linePaint);

        y1 = y1 + 60;
        canvas.drawText("Atomic habit", xItem, y1, paintData);
        canvas.drawText("1", xQty, y1, paintData);
        canvas.drawText("00.0", xRate, y1, paintData);
        canvas.drawText("00.0", xTotal, y1, paintData);
        canvas.drawLine(startX, y1+20, stopX, y1+20, linePaint);

        y1 = y1 + 60;
        canvas.drawText("Do it Today ", xItem, y1, paintData);
        canvas.drawText("1", xQty, y1, paintData);
        canvas.drawText("00.0", xRate, y1, paintData);
        canvas.drawText("00.0", xTotal, y1, paintData);
        canvas.drawLine(startX, y1+20, stopX, y1+20, linePaint);

        y1 = y1 + 60;
        canvas.drawText("It end with us", xItem, y1, paintData);
        canvas.drawText("1", xQty, y1, paintData);
        canvas.drawText("00.0", xRate, y1, paintData);
        canvas.drawText("00.0", xTotal, y1, paintData);
        canvas.drawLine(startX, y1+20, stopX, y1+20, linePaint);

        y1=y1+50;

        // Define the right margin and spacing between the label and the value
        float spacing = 100;   // Space between the label and the value
        rightMargin = rightMargin + 50 ;

        // Measure the width of the labels
        float subtotalTextWidth = paintColumnTitle.measureText("SUBTOTAL AMOUNT:");
        float taxTextWidth = paintColumnTitle.measureText("GST:");
        float totalAmountTextWidth = paintColumnTitle.measureText("TOTAL AMOUNT:");

        // Calculate the X position for right alignment of the labels
        float xSubtotal = mypageInfo.getPageWidth() - subtotalTextWidth - rightMargin - spacing;
        float xTax = mypageInfo.getPageWidth() - taxTextWidth - rightMargin - spacing;
        float xTotalAmount = mypageInfo.getPageWidth() - totalAmountTextWidth - rightMargin - spacing;

        // Calculate the X position for the values (placed slightly to the right of the labels)
        float xValue = mypageInfo.getPageWidth() - rightMargin;  // Aligning the value to the right edge
        float yPosition = y1;

        // Subtotal section
        canvas.drawText("SUBTOTAL AMOUNT:", xSubtotal, yPosition, paintColumnTitle);
        canvas.drawText("TAX:", xTax, yPosition + 30, paintColumnTitle);
        canvas.drawText("TOTAL AMOUNT:", xTotalAmount, yPosition + 60, paintColumnTitle);

        canvas.drawText("00.00", xValue, yPosition, paintData);
        canvas.drawText("00.00", xValue, yPosition + 30, paintData);
        canvas.drawText("00.00", xValue, yPosition + 60, paintData);

        canvas.drawBitmap(textToImageEncode("https://www.youtube.com/@bookshelf_TheNewJourney"), xTotal, yPosition + 60, null);

        String termCnd = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, reprehenderit inon proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        canvas.drawText("TERMS AND CONDITIONS", startX, yPosition + 100, paintColumnTitle);

        int startIndex = 0;
        int endIndex;
        float lineSpacing = 20;  // Space between lines
        float maxWidth = 430;

        // Loop to break and draw text in lines
        while (startIndex < termCnd.length()) {
            endIndex = paintData.breakText(termCnd, startIndex, termCnd.length(), true, maxWidth, null);
            canvas.drawText(termCnd, startIndex, startIndex + endIndex, startX, yPosition+120, paintData);
            yPosition += lineSpacing;
            startIndex += endIndex;
        }

        pdfDocument.finishPage(myPage);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BillPdf.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(MainActivity.this, "PDF Bill file generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Failed to generate PDF file.", Toast.LENGTH_SHORT).show();
        }

        openPDF(file);
        pdfDocument.close();

    }

    private void openPDF(File file) {
        if (file.exists()) {

            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No PDF viewer found!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "PDF file does not exist!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // No permission needed for Android 10+
            return true;
        } else {
            // Check read/write permissions for Android 9 and below
            int readPermission = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
            int writePermission = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
            return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // Request read/write permissions for Android 9 and below
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE,
                    WRITE_EXTERNAL_STORAGE
            }, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                  boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                  boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    public final Bitmap textToImageEncode(@NotNull String Value) {
        BitMatrix mMatrix = null;
        MultiFormatWriter mWriter = new MultiFormatWriter();
        try {
            mMatrix = mWriter.encode(Value, com.google.zxing.BarcodeFormat.QR_CODE, 200, 200);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        BarcodeEncoder mEncoder = new BarcodeEncoder();
        Bitmap mBitmap = mEncoder.createBitmap(mMatrix);
        return mBitmap;
    }

}