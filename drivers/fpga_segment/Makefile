.SILENT:

include C:/AndroXStudio/Tools/make_adb

export ARCH=arm
export CROSS_COMPILE=arm-linux-androideabi-

ifneq ($(KERNELRELEASE),)
obj-m := fpga_segment.o
else
KDIR := /platform/linux-s4412
all:
	$(MAKE) -C $(KDIR) M=$(shell pwd) modules

	echo 
	echo "**** Install:" /system/lib/modules/fpga_segment.ko "****"
	$(ADB) push fpga_segment.ko /system/lib/modules/
	$(ADB) shell chmod 644 /system/lib/modules/fpga_segment.ko
	echo 
	echo "**** Load Module:" /system/lib/modules/fpga_segment.ko "****"
	$(ADB) shell toolbox rmmod fpga_segment > /dev/null
	$(ADB) shell insmod /system/lib/modules/fpga_segment.ko
	$(ADB) shell lsmod | grep fpga_segment
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
