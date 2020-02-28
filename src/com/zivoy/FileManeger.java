package com.zivoy;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileManeger {
    String Filename;

    FileManeger(String Filename) {
        this.Filename = Filename;
        File tempFile = new File(this.Filename);
        if (!tempFile.exists()) {
            try {
                tempFile.createNewFile();
            } catch (IOException ignored) {
            }
        }
    }

    public void save(PrivateKey privateKey, PublicKey publicKey, DefaultListModel<Element> model) {
        try {
            PrintWriter writer = new PrintWriter(this.Filename, "UTF-8");

            writer.println(privateKey);
            writer.println(publicKey);

            for (Object contact : model.toArray()) {
                Element contactElement = (Element) contact;

                StringBuilder line = new StringBuilder();
                if (contactElement.getKeyValue() instanceof PrivateKey) line.append("private");
                else line.append("public");
                line.append(" ");
                line.append(contactElement.name);
                line.append(" ");
                line.append(contactElement.getKeyValue().getKey());

                writer.println(line.toString());
            }
            writer.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public loadout load() {
        try {
            return load(Filename);
        } catch (IOException ignored) {
        }
        PrivateKey temp = new PrivateKey();
        return new loadout(temp, temp.makePublic(), new DefaultListModel<>());
    }

    private loadout load(String filename) throws IOException {
        File file = new File(filename);

        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        PrivateKey privateKey = null;
        PublicKey publicKey = null;
        DefaultListModel<Element> contacts = new DefaultListModel<>();
        long lines = Files.lines(file.toPath()).count();
        for (int i = 0; i < Math.max(lines, 2); i++) {
            st = br.readLine();
            switch (i) {
                case 0:
                    if (st != null)
                        privateKey = PrivateKey.fromString(st);
                    else
                        privateKey = new PrivateKey();
                    break;
                case 1:
                    if (st != null)
                        publicKey = PublicKey.fromString(st);
                    else
                        publicKey = privateKey.makePublic();
                    break;
                default:
                    if (st == null) break;
                    Element item;
                    try {
                        item = StringElement(st, i - 1);
                    } catch (BadLine e) {
                        continue;
                    }
                    contacts.addElement(item);
            }
            System.out.println(st);
        }
        return new loadout(privateKey, publicKey, contacts);
    }

    Element StringElement(String line, int curridx) throws BadLine {
        final Pattern lineRegex = Pattern.compile("(private|public) (.+) (.+)");
        Matcher lineComponents = lineRegex.matcher(line);
        if (!lineComponents.matches()) {
            throw new BadLine();
        }
        String name = lineComponents.group(2);
        String key = lineComponents.group(3);
        Key keyObj;
        switch (lineComponents.group(1)) {
            case "private":
                keyObj = PrivateKey.fromString(key);
                break;
            case "public":
                keyObj = PublicKey.fromString(key);
                break;
            default:
                throw new BadLine();
        }
        return new Element(name, keyObj, curridx);

    }

    static class loadout {
        PrivateKey privateKey;
        PublicKey publicKey;
        DefaultListModel<Element> model;

        loadout(PrivateKey privateKey, PublicKey publicKey, DefaultListModel<Element> model) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
            this.model = model;
        }
    }
}
