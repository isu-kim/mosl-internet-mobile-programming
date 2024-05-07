.SILENT:

#include C:/AndroXStudio/Tools/make_adb

export ARCH=arm
export CROSS_COMPILE=arm-linux-androideabi-

ifneq ($(KERNELRELEASE),)
obj-m := keypad_driver.o
else
KDIR := ../../platform/linux
all:
	echo $(MAKE) -C $(KDIR) M=$(shell pwd) modules
	

	echo 
	echo "**** Install:" /system/lib/modules/keypad_driver.ko "****"
	$(ADB) push keypad_driver.ko /system/lib/modules/
	$(ADB) shell chmod 644 /system/lib/modules/keypad_driver.ko
	echo 
	echo "**** Load Module:" /system/lib/modules/keypad_driver.ko "****"
	$(ADB) shell toolbox rmmod keypad_driver > /dev/null
	$(ADB) shell insmod /system/lib/modules/keypad_driver.ko
	$(ADB) shell lsmod | grep keypad_driver
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
