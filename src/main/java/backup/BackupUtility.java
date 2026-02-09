package backup;

import setup.DBConfig;

import java.io.*;

public class BackupUtility {

    public static boolean backup(String dbName, String outputFile) {
        try {
            String command = String.format(
                    "mysqldump -h%s -P%s -u%s -p%s %s",
                    DBConfig.host, DBConfig.port, DBConfig.user, DBConfig.pass,
                    dbName);

            ProcessBuilder pb;

            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                pb = new ProcessBuilder("cmd.exe", "/c", command);
            } else {
                pb = new ProcessBuilder("bash", "-c", command);
            }

            pb.redirectOutput(new File(outputFile));

            pb.redirectErrorStream(false);

            Process p = pb.start();
            int exit = p.waitFor();

            if (exit != 0) {
                System.out.println("Backup failed. Exit code = " + exit);
                return false;
            }

            System.out.println("Backup completed successfully!");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean restore(String dbName, String inputFile) {
        try {
            String mysqlExec = "mysql";
            java.util.List<String> cmd = new java.util.ArrayList<>();
            cmd.add(mysqlExec);
            cmd.add("-h");
            cmd.add(DBConfig.host);
            cmd.add("-P");
            cmd.add(DBConfig.port);
            cmd.add("-u");
            cmd.add(DBConfig.user);
            cmd.add("-p" + DBConfig.pass);
            cmd.add(dbName);

            ProcessBuilder pb = new ProcessBuilder(cmd);

            pb.redirectInput(new File(inputFile));
            pb.redirectErrorStream(true);

            Process p = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));

            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exit = p.waitFor();

            System.out.println("---- RESTORE OUTPUT ----");
            System.out.println(output);
            System.out.println("------------------------");

            if (exit != 0) {
                System.err.println("Restore failed. Exit code = " + exit);
                return false;
            }

            System.out.println("Restore completed successfully!");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
