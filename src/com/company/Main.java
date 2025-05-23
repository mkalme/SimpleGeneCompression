//221RDB411, Miķelis Kalme-Danenbaums, 14. grupa

package com.company;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while(callCommand(scanner.nextLine()));

        scanner.close();
    }

    private static boolean callCommand(String line){
        String signature = getSignature(line);

        switch (signature){
            case exitSignature:
                return false;
            case aboutSignature:
                callAboutCommand();
                break;
            case compSignature:
                callCompCommand(line);
                break;
            case decompSignature:
                callDecompCommand(line);
                break;
            default:
                System.out.println(wrongCommandOutput);
                break;
        }

        return true;
    }
    private static String getSignature(String line){
        int spaceIndex = line.indexOf(' ');

        if(spaceIndex < 0) return line;
        return line.substring(0, spaceIndex);
    }

    //region exit
    private static final String exitSignature = "exit";
    private static final String wrongCommandOutput = "wrong command";
    //endregion

    //region about
    private static final String aboutSignature = "about";
    private static final String information = "221RDB411, Miķelis Kalme-Danenbaums, 14. grupa";

    private static void callAboutCommand(){
        System.out.println(getInformation());
    }
    private static String getInformation(){
        return information;
    }
    //endregion

    //region comp
    private static final String compSignature = "comp";
    private static final String wrongCommandFormatOutput = "wrong command format";

    private static void callCompCommand(String userInput){
        String geneSequence = userInput.substring(compSignature.length() + 1);

        if(!validateGeneSequence(geneSequence)){
            System.out.println(wrongCommandFormatOutput);
            return;
        }

        byte[] compressedGeneSequence = compress(geneSequence);
        String hexSequence = convertByteArrayToHexSequence(compressedGeneSequence);

        System.out.println(hexSequence);
    }

    private static boolean validateGeneSequence(String geneSequence){
        for(int i = 0; i < geneSequence.length(); i++){
            if(!isGeneValid(geneSequence.charAt(i))) return false;
        }

        return true;
    }
    private static boolean isGeneValid(char gene){
        switch (Character.toUpperCase(gene)){
            case 'A': case 'C': case 'G': case 'T': return true;
            default: return false;
        }
    }

    private static byte[] compress(String geneSequence){
        int length = (int)Math.ceil(2 * geneSequence.length() / 8d);
        byte[] output = new byte[length + 1];
        output[0] = (byte)geneSequence.length();

        for(int i = 0; i < geneSequence.length(); i++){
            int byteIndex = i / 4 + 1;
            int bitOffset = 6 - (i % 4 * 2);

            char gene = geneSequence.charAt(i);

            output[byteIndex] |= encode(gene) << bitOffset;
        }

        return output;
    }
    private static byte encode(char gene){
        switch (gene){
            case 'A': return 0;
            case 'C': return 1;
            case 'G': return 2;
            case 'T': return 3;
            default: return -1;
        }
    }

    private static String convertByteArrayToHexSequence(byte[] byteArray){
        StringBuilder builder = new StringBuilder();

        for(byte value : byteArray){
            builder.append(convertByteToHex(value));
            builder.append(" ");
        }

        return builder.toString();
    }
    private static String convertByteToHex(byte value){
        return String.format("%1X", value);
    }
    //endregion

    //region decomp
    private static final String decompSignature = "decomp";

    private static void callDecompCommand(String userInput){
        try{
            String byteSequence = userInput.substring(decompSignature.length() + 1);
            byte[] compressedGeneSequence = convertStringToByteArray(byteSequence);

            String hexSequence = convertByteArrayToHexSequence(compressedGeneSequence);
            String geneSequence = decompress(compressedGeneSequence);

            System.out.println(hexSequence);
            System.out.println(geneSequence);
        }catch(Exception e) {
            System.out.println(wrongCommandFormatOutput);
        }
    }
    private static byte[] convertStringToByteArray(String string){
        String[] bytes = string.split(" ");
        byte byteCount = Byte.decode(bytes[0]);

        byte[] output = new byte[byteCount];
        for(int i = 0; i < byteCount; i++){
            output[i] = Byte.decode(bytes[i + 1]);
        }

        return output;
    }

    private static String decompress(byte[] compressedGeneSequence){
        char[] geneSequence = new char[compressedGeneSequence[0]];

        for(int i = 0; i < geneSequence.length; i++){
            int byteIndex = i / 4 + 1;
            int bitOffset = 6 - (i % 4 * 2);

            int encodedGene = (compressedGeneSequence[byteIndex] & (0x3 << bitOffset)) >> bitOffset;
            char gene = decode(encodedGene);

            geneSequence[i] = gene;
        }

        return new String(geneSequence);
    }
    private static char decode(int encodedGene){
        switch (encodedGene){
            case 0: return 'A';
            case 1: return 'C';
            case 2: return 'G';
            case 3: return 'T';
            default: return Character.MIN_VALUE;
        }
    }
    //endregion
}
