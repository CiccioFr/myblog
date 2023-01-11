package it.cgmconsulting.myblog.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;

@Service
public class FileService {

    /**
     * verifica dellle dimenzioni in peso
     *
     * @param file
     * @param size
     * @return
     */
    public boolean checkSize(MultipartFile file, long size) {
        //file.getSize()
        return !file.isEmpty() && file.getSize() <= size;
    }

    //1126
    public BufferedImage fromMultipartFileToBufferedImage(MultipartFile file) {
        BufferedImage bf = null;
        try {
            bf = ImageIO.read(file.getInputStream());
            return bf;
        } catch (IOException e) {
            return null;
        }
    }

    public boolean checkDimension(BufferedImage bf, int width, int height) {
        if (bf == null)
            return false;
        return bf.getHeight() <= height && bf.getWidth() <= width;
    }

    // per le estensioni, ci passa lui il metodo lungo e rognoso, affinato nei vari corsi
    public boolean checkExtension(MultipartFile file, String[] extensions) {
        // ImageInputStream ha al suo interno un oggetto iteratore che contiene image reader che contiene il mimeType, che verifico se contento nel mio array di stringhe
        ImageInputStream img = null;
        try {
            img = ImageIO.createImageInputStream(file.getInputStream());
        } catch (IOException e) {
            return false;
        }

        Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(img);

        while (imageReaders.hasNext()) {
            ImageReader reader = imageReaders.next();
            try {
                for (int i = 0; i < extensions.length; i++) {
                    if (reader.getFormatName().equalsIgnoreCase(extensions[i])) {
                        return true;
                    }
                }
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

}
