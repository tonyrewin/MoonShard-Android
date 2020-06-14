package io.moonshard.moonshard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;

import java.io.FileOutputStream;
import java.io.IOException;

public class ViewPrintAdapter extends PrintDocumentAdapter {

    private PrintedPdfDocument mDocument;
    private Context mContext;

    Context context;
    private int pageHeight;
    private int pageWidth;
    public PdfDocument myPdfDocument;
    public int totalpages = 1;

    Bitmap qrCode;
    String nameEvent;
    String typeEvent;
    String eventStartDate;
    String addressEvent;
    String idTicket;

    public ViewPrintAdapter(Context context, Bitmap qrCode, String nameEvent, String typeEvent, String eventStartDate, String addressEvent,String idTicket) {
        mContext = context;
        this.qrCode = qrCode;
        this.nameEvent = nameEvent;
        this.typeEvent = typeEvent;
        this.eventStartDate = eventStartDate;
        this.addressEvent = addressEvent;
        this.idTicket = idTicket;

    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback callback, Bundle extras) {

        myPdfDocument = new PrintedPdfDocument(context, newAttributes);

        pageHeight =
                newAttributes.getMediaSize().getHeightMils() / 1000 * 72;
        pageWidth =
                newAttributes.getMediaSize().getWidthMils() / 1000 * 72;

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        if (totalpages > 0) {
            PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                    .Builder(nameEvent+idTicket+".pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(totalpages);

            PrintDocumentInfo info = builder.build();
            callback.onLayoutFinished(info, true);
        } else {
            callback.onLayoutFailed("Page count is zero.");
        }
    }

    @Override
    public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal,
                        WriteResultCallback callback) {

        PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                pageHeight, 0).create();

        PdfDocument.Page page =
                myPdfDocument.startPage(newPage);

        if (cancellationSignal.isCanceled()) {
            callback.onWriteCancelled();
            myPdfDocument.close();
            myPdfDocument = null;
            return;
        }
        drawPage(page);
        myPdfDocument.finishPage(page);

        try {
            myPdfDocument.writeTo(new FileOutputStream(
                    destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            myPdfDocument.close();
            myPdfDocument = null;
        }

        callback.onWriteFinished(pageRanges);
    }

    private void drawPage(PdfDocument.Page page) {
        Canvas canvas = page.getCanvas();

        float centreX = (canvas.getWidth() - qrCode.getWidth()) / 2;

        float centreY = (canvas.getHeight() - qrCode.getHeight()) / 2;

        int titleBaseLine = 72;
        int leftMargin = 54;

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        canvas.drawText(
                nameEvent,
                centreX,
                titleBaseLine,
                paint);

        paint.setTextSize(14);
        canvas.drawText("Тип билета: " + typeEvent, leftMargin, titleBaseLine + 35, paint);
        canvas.drawText("Время начала события: " + eventStartDate, leftMargin, titleBaseLine + 70, paint);
        canvas.drawText("Адресс события: " + addressEvent, leftMargin, titleBaseLine + 105, paint);
        canvas.drawText("Номер билета: " + idTicket, leftMargin, titleBaseLine + 135, paint);

        canvas.drawBitmap(qrCode, centreX,
                centreY, paint);
    }
}
