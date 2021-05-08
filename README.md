# VirGL vtest Launcher
VirGL vtest Launcher - Experimental launcher for virgl vtest, zink and Turnip to provide 3D acceleration to ExaGear on Snapdragon devices.

## What is VirGL vtest Launcher and what does it do?
VirGL vtest Launcher is an experimental application which chroots into an Ubuntu 20.04 ARM64 rootfs with the OpenSource Turnip Adreno Vulkan driver, Zink and virgl vtest server preinstalled. Afterwards it launches the virgl vtest server and uses Turnip as the Vulkan driver and zink for OpenGL over Vulkan wrapping. The vtest server then forwards and receives OpenGL calls to/from the ExaGear client side.

## What is currently supported?
Zink ontop of Turnip currently exposes OpenGL 3.0 on an Adreno 650 GPU which should be enough to support quite a few games, the results may differ on other Adreno GPUs. However Turnip and Zink are still having some issues, so many games will not render at all or show graphical artifacts.

## Known issues:
* virgl causes significant slowdowns / performance losses
* DirectX9+ games and software will most likely only render a black screen
* Games might suffer from vertex explosions and/or other graphical artifacts

## Things to note:
* This app requires root
* Only Adreno GPUs are supported (unless you want to use software rendering)

## Quick installation guide:
* Download and install [BusyBox](https://play.google.com/store/apps/details?id=stericson.busybox&hl=de&gl=US) from the PlayStore. Note that BusyBox might get uninstalled on rebooting your device, so please always make sure if it is installed before using the launcher.
* Set SELinux to permissive, either by running **_su -c setenforce 0_** in a terminal or by using the SELinux mode changer.
* Download the APK from the releases tab, install it, launch it and give it root permissions, then close it again.
* Download the .obb cache from the releases tab and copy it to /storage/emulated/0/Android/obb/lu.grima04.virglvtestlauncher
* Now, launch the app again, let it extract the rootfs and wait until it finishes.
* Afterwards, download the libGL.so.1 shared library from the releases tab, copy it to /data/data/com.eltechs.ed/files/image/usr/lib/i386-linux-gnu
* If everything works as planned, you can now press the START button on the launcher app
* Now you are able to open ExaGear and launch your games
* When you are done, please do not forget to press STOP to properly kill all the background processes, otherwise they will continue running in the background and drain your battery

## Disclaimer
**I am not responsible for any damage, harm, file losses, etc that might occur to you, your device or other belongings by using this app. Use at your own risks. The emulation process of ExaGear (and virgl) is very taxing on the CPU and GPU, so _always_ keep your CPU, GPU and battery temperatures under close observation**
