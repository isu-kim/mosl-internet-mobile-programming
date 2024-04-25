.SILENT:

#include C:/AndroXStudio/Tools/make_adb

export ARCH=arm
export CROSS_COMPILE=arm-linux-androideabi-

ifneq ($(KERNELRELEASE),)
obj-m := fpga_led.o
else
KDIR := ../../platform/linux
all:
	echo $(MAKE) -C $(KDIR) M=$(shell pwd) modules
	

	echo 
	echo "**** Install:" /system/lib/modules/fpga_led.ko "****"
	$(ADB) push fpga_led.ko /system/lib/modules/
	$(ADB) shell chmod 644 /system/lib/modules/fpga_led.ko
	echo 
	echo "**** Load Module:" /system/lib/modules/fpga_led.ko "****"
	$(ADB) shell toolbox rmmod fpga_led > /dev/null
	$(ADB) shell insmod /system/lib/modules/fpga_led.ko
	$(ADB) shell lsmod | grep fpga_led
	echo

endif

clean:
	rm -f *.symvers
	rm -f *.ko
	rm -f *.o
	rm -f *.mod.c
	rm -f *.order
	rm -f .*.cmd
	rm -rf .tmp_versions
