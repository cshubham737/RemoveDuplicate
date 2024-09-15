package utility.com;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

public class RemoveDuplicate {

    public static void main(String[] args) {
        Path folder1 = Paths.get("C:\\Users\\ShubhamChaturvedi\\Downloads\\ResumeUpload");
        Path folder2 = Paths.get("C:\\Users\\ShubhamChaturvedi\\Downloads\\ResumeFromLatest");

        try {
            removeDuplicatesAndSaveInSmallerFolder(folder1, folder2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeDuplicatesAndSaveInSmallerFolder(Path folder1, Path folder2) throws IOException {
        // Count the number of files in both folders
        int folder1FileCount = countFiles(folder1);
        int folder2FileCount = countFiles(folder2);

        // Determine which folder has more files
        Path largerFolder = folder1FileCount >= folder2FileCount ? folder1 : folder2;
        Path smallerFolder = folder1FileCount < folder2FileCount ? folder1 : folder2;

        // Collect files from both folders
        Set<String> largerFolderFiles = collectFilePaths(largerFolder);
        Set<String> smallerFolderFiles = collectFilePaths(smallerFolder);

        // Iterate through files in the larger folder and remove duplicates from the larger folder
        for (String relativePath : largerFolderFiles) {
            Path fileInLargerFolder = largerFolder.resolve(relativePath);
            Path fileInSmallerFolder = smallerFolder.resolve(relativePath);

            if (Files.exists(fileInSmallerFolder) && Files.exists(fileInLargerFolder)) {
                // Compare the content of the files
                if (Files.isSameFile(fileInLargerFolder, fileInSmallerFolder) || compareFileContents(fileInLargerFolder, fileInSmallerFolder)) {
                    // Remove duplicate from the larger folder
                    Files.delete(fileInLargerFolder);
                    System.out.println("Deleted duplicate: " + fileInLargerFolder);
                }
            }
        }

        System.out.println("Duplicate removal from larger folder completed.");
    }

    // Count the number of files in the folder
    public static int countFiles(Path folder) throws IOException {
        int[] count = {0}; // Using an array to allow modification inside lambda

        Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                count[0]++;
                return FileVisitResult.CONTINUE;
            }
        });

        return count[0];
    }

    // Collect all file paths in the folder
    public static Set<String> collectFilePaths(Path folder) throws IOException {
        Set<String> filePaths = new HashSet<>();

        Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // Collect relative paths
                filePaths.add(folder.relativize(file).toString());
                return FileVisitResult.CONTINUE;
            }
        });

        return filePaths;
    }

    // Compare the content of two files
    public static boolean compareFileContents(Path file1, Path file2) throws IOException {
        byte[] file1Bytes = Files.readAllBytes(file1);
        byte[] file2Bytes = Files.readAllBytes(file2);
        return java.util.Arrays.equals(file1Bytes, file2Bytes);
    }
}
