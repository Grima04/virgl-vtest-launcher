# VirGL vtest Launcher
VirGL vtest Launcher - Experimental launcher for virgl vtest, zink and Turnip to provide 3D acceleration to ExaGear on Snapdragon devices.

## What is VirGL vtest Launcher and what does it do?
VirGL vtest Launcher is an experimental application which chroots into an Ubuntu 20.04 ARM64 rootfs with the OpenSource Turnip Adreno Vulkan driver, Zink and virgl vtest server preinstalled. Afterwards it launches the virgl vtest server and uses Turnip as the Vulkan driver and zink for OpenGL over Vulkan wrapping. The vtest server then forwards and receives OpenGL calls to/from the ExaGear client side.

## What is currently supported?
Zink ontop of Turnip currently exposes OpenGL 3.0 and OpenGL ES 3.1 on an Adreno 650 GPU which should be enough to support quite a few games, the results may differ on other Adreno GPUs. However Turnip and Zink are still having some issues, so many games will not render at all or show graphical artifacts.

## Known issues:
* virgl causes significant slowdowns / performance losses.
* DirectX9+ games and software will most likely only render a black screen when using Zink's OpenGL backend. Please use Zink's OpenGL ES backend as a temporary workaround.
* Games might suffer from vertex explosions and/or other graphical artifacts due to a non implemented Vulkan extension in the Turnip driver.

## Things to note:
* This app requires root.
* You **need** the specific BusyBox version linked in the quick installation guide. Other BusyBox versions like the BusyBox Magisk module might not work.
* Only Adreno GPUs are supported (unless you want to use software rendering).
* A relatively recent **Snapdragon** Android device **with the DRM/DRI GPU interface** is needed (check if /dev/dri/card0 exists on the device).

## Quick installation guide:
* Download and install [BusyBox](https://play.google.com/store/apps/details?id=stericson.busybox&hl=de&gl=US) from the PlayStore. Note that BusyBox might get uninstalled on rebooting your device, so please always make sure if it is installed before using the launcher.
* Set SELinux to permissive if the vtest server launches but no connection can be established from the ExaGear side, either by running **_su -c setenforce 0_** in a terminal or by using the SELinux mode changer.
* Download the APK from the releases tab, install it, launch it and give it root permissions, then close it again.
* Download the .obb cache from the releases tab and copy it to /storage/emulated/0/Android/obb/lu.grima04.virglvtestlauncher
* Now, launch the app again, let it extract the rootfs and wait until it finishes.
* Afterwards, download the libGL.so.1 shared library from the releases tab, copy it to /data/data/com.eltechs.ed/files/image/usr/lib/i386-linux-gnu
* If everything works as planned, you can now press the START button on the launcher app
* Now you are able to open ExaGear and launch your games
* When you are done, please do not forget to press STOP to properly kill all the background processes, otherwise they will continue running in the background and drain your battery
* Also remember to set SELinux back to enforcing as soon as you are done using the app for security reasons

## Gallery (all tests performed on an Adreno 650 GPU)
* OpenGL 3.2 Mesh Exploder demo (GPU Caps Viewer) [Unstable framerate between 50 and 110 FPS, 1280x681 windowed]
![](https://github.com/Grima04/virgl-vtest-launcher/blob/master/images/Screenshot_2021-06-01-15-10-16-258_com.eltechs.ed.jpg?raw=true)
* OpenGL 3.2 Shadertoy Seascape demo (GPU Caps Viewer) [Around 16 FPS, 1280x681 windowed]
![](https://github.com/Grima04/virgl-vtest-launcher/blob/master/images/Screenshot_2021-06-01-14-55-39-935_com.eltechs.ed.jpg?raw=true)
* OpenGL 3.2 Spherical Env Mapping demo (GPU Caps Viewer) [Around 70 to 100 FPS, 1280x681 windowed]
![](https://github.com/Grima04/virgl-vtest-launcher/blob/master/images/Screenshot_2021-06-01-15-03-36-699_com.eltechs.ed.jpg?raw=true)
* OpenGL 2.1 Shadertoy Radial Blur demo (GPU Caps Viewer) [Around 11 to 15 FPS, 1280x681 windowed]
![](https://github.com/Grima04/virgl-vtest-launcher/blob/master/images/Screenshot_2021-06-01-15-04-55-901_com.eltechs.ed.jpg?raw=true)
* OpenGL wglgears demo (glxgears Windows port)
![](https://github.com/Grima04/virgl-vtest-launcher/blob/master/images/Screenshot_2021-06-01-14-50-53-400_com.eltechs.ed.jpg?raw=true)


## Third party software:
* [AnLinux Ubuntu rootfs](https://github.com/EXALAB/Anlinux-Resources/tree/master/Rootfs/Ubuntu/arm64) GPL-2.0 License
* [Mesa (includes Turnip, virgl and zink)](https://github.com/mesa3d/mesa) MIT License and other GPL2 compatible licenses
* [virgl vtest](https://gitlab.freedesktop.org/virgl/virglrenderer) MIT License

## Disclaimer
**I am not responsible for any damage, harm, file losses, etc that might occur to you, your device or other belongings by using this app. Use at your own risks. The emulation process of ExaGear (and virgl) is very taxing on the CPU and GPU, so _always_ keep your CPU, GPU and battery temperatures under close observation**
