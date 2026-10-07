package me.mioclient.util.io;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

public class ImageTransferable implements Transferable {
    public Image image;

    public ImageTransferable(Image image) {
        this.image = image;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DataFlavor.imageFlavor};
    }

    public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
        return DataFlavor.imageFlavor.equals(dataFlavor);
    }

    public Object getTransferData(DataFlavor dataFlavor) throws java.awt.datatransfer.UnsupportedFlavorException {
        if (DataFlavor.imageFlavor.equals(dataFlavor)) {
            return this.image;
        }
        throw new UnsupportedFlavorException(dataFlavor);
    }
}
