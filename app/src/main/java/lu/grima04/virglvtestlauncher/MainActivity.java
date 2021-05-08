/*      VirGL vtest Launcher - Experimental launcher for virgl vtest, zink and Turnip to provide 3D acceleration to ExaGear on Snapdragon devices.
        Copyright (C) 2021 Grima04

        This program is free software; you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation; either version 2 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License along
        with this program; if not, write to the Free Software Foundation, Inc.,
        51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/


package lu.grima04.virglvtestlauncher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    protected static boolean isRooted = false;
    protected static ProcessBuilder setupProcessBuilder = null;
    protected static Process setupProcess = null;
    protected TextView terminal;
    protected static ProcessBuilder emulationProcessBuilder = null;
    protected static Process emulationProcess = null;
    protected static BufferedWriter shellWriter = null;
    protected static boolean printConsole = true;
    protected static Button start;
    protected static Button stop;
    protected static Button glxgears;
    protected static Switch useSoftwareRendering;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        terminal = (TextView) findViewById(R.id.terminalView);
        terminal.setMovementMethod(new ScrollingMovementMethod());
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        glxgears = (Button) findViewById(R.id.glxgears);
        useSoftwareRendering = (Switch) findViewById(R.id.softwareRendering);
        System.out.println("Files directory: " + getFilesDir());
        System.out.println("OBB directory: " + getApplicationContext().getObbDir());

        try {
            if(isDirEmpty(Paths.get("/data/data/lu.grima04.virglvtestlauncher/files")) && isDeviceRooted()==true){
                extractOBB();
            }else if (isDeviceRooted()==true){
                Toast toastSetup = Toast.makeText(getApplicationContext(),"Root detected successfully!",Toast.LENGTH_SHORT);
                toastSetup.show();
            }else if (isDeviceRooted() == false){
                Toast toastSetup = Toast.makeText(getApplicationContext(),"Error: your device is not rooted!",Toast.LENGTH_SHORT);
                toastSetup.show();
            }else{
                Toast toastSetup = Toast.makeText(getApplicationContext(),"Unknown error occurred!",Toast.LENGTH_SHORT);
                toastSetup.show();
            }
        } catch (Exception e) {
            Toast toastSetup = Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
            toastSetup.show();
            e.printStackTrace();
        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEmulation("virgl");
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //Kill all processes related to vtest
                    Process killx11 = Runtime.getRuntime().exec("su -c killall glxgears && su -c killall Xvfb && su -c killall virgl_test_server && su -c killall grep");
                    printConsole = false;
                    emulationProcess.destroy();
                    terminal.setText("Stopped all background processes!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        glxgears.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEmulation("glxgears");
            }
        });
    }


    private static void printConsoleOutput(Process process, TextView textView){
        BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String consoleText = null;
                    while((consoleText = outputReader.readLine()) != null){
                        textView.setText(consoleText+"\n");
                        System.out.println(consoleText);
                    }
                    while((consoleText = errorReader.readLine()) != null){
                        textView.setText(consoleText+"\n");
                        System.out.println(consoleText);
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                } catch (Exception f){
                    //
                }

            }
        });
        thread.start();
    }

    private static boolean isDirEmpty(final Path directory) throws IOException {
        //Check if the Ubuntu rootfs exists
        try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
        }
    }

    private static boolean isDeviceRooted(){
        //Check for root rights
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"su", "-c", "id"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output = in.readLine();
            System.out.println(output);
            if (output != null && output.toLowerCase().contains("uid=0")) return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) process.destroy();
        }return false;
    }

    private void extractOBB(){
        try{
            //If rootfs is empty on application launch -> extract the obb containing the rootfs
            Toast toastSetup = Toast.makeText(getApplicationContext(),"Setting up emulation environment.\nPlease wait...",Toast.LENGTH_LONG);
            toastSetup.show();
            setupProcessBuilder = new ProcessBuilder("/bin/sh");
            setupProcess = setupProcessBuilder.start();
            BufferedWriter shellWriter = new BufferedWriter(new OutputStreamWriter(setupProcess.getOutputStream()));
            //Runtime.getRuntime().exec("su -c mkdir /data/data/lu.grima04.virglvtestlauncher/files && su -c mkdir /sdcard/Android/obb/lu.grima04.virglvtestlauncher");
            shellWriter.write("su");
            shellWriter.flush();
            shellWriter.newLine();
            shellWriter.write("cp /sdcard/Android/obb/lu.grima04.virglvtestlauncher/rootfs.obb /data/data/lu.grima04.virglvtestlauncher/files");
            shellWriter.newLine();
            shellWriter.flush();
            shellWriter.write("cd /data/data/lu.grima04.virglvtestlauncher/files");
            shellWriter.newLine();
            shellWriter.flush();
            shellWriter.write("unzip rootfs.obb");
            shellWriter.newLine();
            shellWriter.flush();
            shellWriter.write("rm rootfs.obb");
            shellWriter.newLine();
            shellWriter.flush();
            if(new File("/storage/emulated/0/Android/obb/lu.grima04.virglvtestlauncher/rootfs.obb").exists() == false){
                shellWriter.write("echo 'Error! The OBB cache file does not exist! Please install the OBB cache and restart the app.'");
                shellWriter.newLine();
                shellWriter.flush();
            }else{
                shellWriter.write("echo 'Done! You can now safely start the VirGL vtest server'");
                shellWriter.newLine();
                shellWriter.flush();
            }
            shellWriter.write("exit");
            shellWriter.newLine();
            shellWriter.flush();
            printConsoleOutput(setupProcess,terminal);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void startEmulation(String program){
        isRooted = isDeviceRooted();
        if (isRooted){
            Toast toastSuccess = Toast.makeText(getApplicationContext(),"Starting " + program + "...",Toast.LENGTH_SHORT);
            toastSuccess.show();

            try {
                printConsole = true;
                emulationProcessBuilder = new ProcessBuilder("/bin/sh");
                emulationProcess = emulationProcessBuilder.start();
                shellWriter = new BufferedWriter(new OutputStreamWriter(emulationProcess.getOutputStream()));
                //Get root permissions
                shellWriter.write("su");
                shellWriter.newLine();
                shellWriter.flush();
                //cd into the Ubuntu rootfs and start the chroot jail environment via premade script
                shellWriter.write("cd /data/data/lu.grima04.virglvtestlauncher/files && chmod +x chroot.sh && sh chroot.sh");
                shellWriter.newLine();
                shellWriter.flush();
                //Setup needed environment variables
                if (useSoftwareRendering.isChecked()){
                    shellWriter.write("export DISPLAY=:0 && export MESA_GLSL_CACHE_DISABLE=true && export MESA_NO_ERROR=1 && export MESA_GL_VERSION_OVERRIDE=4.6COMPAT");
                    shellWriter.newLine();
                    shellWriter.flush();
                }else{
                    shellWriter.write("export DISPLAY=:0 && export MESA_GLSL_CACHE_DISABLE=true && export MESA_NO_ERROR=1 && export MESA_GL_VERSION_OVERRIDE=4.6COMPAT && export GALLIUM_DRIVER=zink && export MESA_DRIVER_LOADER_OVERRIDE=zink");
                    shellWriter.newLine();
                    shellWriter.flush();
                }

                if(program == "glxgears"){
                    //Workaround with Xvfb to enable offscreen x11 rendering
                    shellWriter.write("Xvfb $DISPLAY -screen 0 800x600x16 &");
                    shellWriter.newLine();
                    shellWriter.flush();
                    //execute glxgears and get the GL_RENDERER String
                    shellWriter.write("sleep 2 && glxgears");
                    shellWriter.newLine();
                    shellWriter.flush();
                    //Print the shell output in the Android Studio Logcat and on the application TextView
                    printConsoleOutput(emulationProcess,terminal);
                }else if(program == "virgl"){
                    //Workaround with Xvfb to enable offscreen x11 rendering
                    shellWriter.write("Xvfb $DISPLAY -screen 0 800x600x16 &");
                    shellWriter.newLine();
                    shellWriter.flush();
                    //cd to the vtest executable
                    shellWriter.write("cd /root/virglrenderer/build/vtest");
                    shellWriter.newLine();
                    shellWriter.flush();
                    //Launch virgl vtest
                    shellWriter.write("./virgl_test_server --use-glx & echo 'Started virgl vtest!' &");
                    //shellWriter.write("glxgears");
                    shellWriter.newLine();
                    shellWriter.flush();
                    Runtime.getRuntime().exec("su -c sleep 5 && su -c chmod 777 /data/data/com.eltechs.ed/files/image/tmp/.virgl_test");
                    //Print the shell output in the Android Studio Logcat and on the application TextView
                    printConsoleOutput(emulationProcess,terminal);
                }

            }catch(Exception e){
                e.printStackTrace();
            }

        }else{
            Toast toastFailed = Toast.makeText(getApplicationContext(),"Failed to start emulation.\nAre you rooted?",Toast.LENGTH_LONG);
            toastFailed.show();
        }
    }

}