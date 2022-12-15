package com.example.todo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class PrintHelper {
    // Helper class for printing, creating pdfs, and creating documents.

    public static void createPDFFile(Context context, String path, TasksCategory list, String author, String creator) {
        //Check if path exists.

        if (new File(path).exists()) {
            new File(path).delete(); //Delete existing path
        }

        try {
            Document document = new Document();

            // Open file to write.
            OutputStream ops = new FileOutputStream(path);
            PdfWriter.getInstance(document, ops);

            //Request access to open to write.
            document.open();

            // Settings
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor(author);
            document.addCreator(creator);

            // Custom font
            BaseFont fontName = BaseFont.createFont("assets/fonts/Redressed-Regular.ttf", "UTF-8", BaseFont.EMBEDDED); //Fetch font, encode in UTF-8.
            float titleFontSize = 36.0f;

            // Font settings.
            BaseColor colorAccent = new BaseColor(204, 153, 204, 255);
            colorAccent = BaseColor.BLACK;
            float fontSize = 20.0f; //For text
            float valueFontSize = 26.0f; //For values
            Font itemFont = new Font(fontName, fontSize, Font.NORMAL, colorAccent);
            Font itemNumberFont = new Font(fontName, fontSize, Font.NORMAL, colorAccent);

            // Document's title
            Font titleFont = new Font(fontName, titleFontSize, Font.NORMAL, BaseColor.BLACK); //Black normal text in specific size and font.
            addNewItem(document, list.getTitle(), Element.ALIGN_CENTER, titleFont);

            // Adding all tasks.
            for(Task t: list.getTasks())
            {
                addLineSeparator(document);
                addNewItem(document, t.getTitle(), Element.ALIGN_LEFT, itemFont);
                addNewItemWithLeftAndRight(document,
                        "Side-note: " + t.getContent(),
                        "Due date: " + t.getStringFromDate(),
                        itemNumberFont, itemNumberFont);
            };

            document.close(); //Finish writing to document.
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void createPDFFileFromName(String fileName, Context context, TasksCategory list, String author, String creator) {
        String downloadPath = Common.getPrintPath(context);
        String path = downloadPath + fileName + ".pdf";
        askForWritePerm(path, context, list, author, creator);
    }

    public static void createPDFFileFromName(String fileName, Context context, TasksCategory list, long user_id) {
        DBHelper db = new DBHelper(context);
        String fullName = db.selectUserByID(user_id).getFullName();

        createPDFFileFromName(fileName, context, list, fullName, fullName);
    }

    private static void askForWritePerm(String path, Context context, TasksCategory list, String author, String creator) {
        Dexter.withContext(context)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        PrintHelper.createPDFFile(context, path, list, author, creator);
                        printDocument(path, context);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                })
                .check();
    }

    public static void printDocument(String path, Context context) {
        PrintManager pm = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        try {
            //Finds the saved file and throws into a document adapter.
            //prints using adapter.
            PrintDocumentAdapter adapter = new PdfDocumentAdapter(context, path);
            PrintJob pj = pm.print("Document", adapter, new PrintAttributes.Builder().build());
            Toast.makeText(context, "successful", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d("print error", e.getMessage());
            e.printStackTrace();
        }
    }

    // Document layout methods.
    public static void addNewItemWithLeftAndRight(Document document, String textLeft, String textRight, Font fontLeft, Font fontRight) throws DocumentException {
        Chunk chunkLeft = new Chunk(textLeft, fontLeft);
        Chunk chunkRight = new Chunk(textRight, fontRight);

        Paragraph p = new Paragraph(chunkLeft); //Construct a paragraph from chunk left.
        p.add(new Chunk(new VerticalPositionMark())); //add vertical spacing
        p.add(chunkRight); //add right chunk to paragraph.
        document.add(p); //Attempt to add paragraph to document.
    }

    public static void addLineSeparator(Document document) throws DocumentException {
        //Adds a line separator to document.
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68)); //We want a transparent shade of black.
        addLineSpace(document);
        document.add(new Chunk(lineSeparator));
        addLineSpace(document);
    }

    public static void addLineSpace(Document document) throws DocumentException {
        //Adds an empty space to document.
        document.add(new Paragraph(""));
    }

    public static void addNewItem(Document document, String text, int alignment, Font font) throws DocumentException {
        //Add a paragraph to a document.
        Chunk chunk = new Chunk(text, font); //Convert to chunk of text.
        Paragraph paragraph = new Paragraph(chunk); //format to paragraph.
        paragraph.setAlignment(alignment); //set alignment.
        document.add(paragraph);

        //Method throws document exception in case of failure.
    }

}
