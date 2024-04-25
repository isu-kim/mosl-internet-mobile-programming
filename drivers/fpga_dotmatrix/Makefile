.SILENT:

include C:/AndroXStudio/Tools/make_adb

export ARCH=arm
export CROSS_COMPILE=arm-linux-androideabi-

ifneq ($(KERNELRELEASE),)
obj-m := fpga_dotmatrix.o
else
KDIR := /platform/linux
all:
	$(MAKE) -C $(KDIR) M=$(shell pwd) modules

	echo 
	echo "**** Install:" /system/lib/modules/fpga_dotmatrix.ko "****"
	$(ADB) push fpga_dotmatrix.ko /system/lib/modules/
	$(ADB) shell chmod 644 /system/lib/modules/fpga_dotmatrix.ko
	echo 
	echo "**** Load Module:" /system/lib/modules/fpga_dotmatrix.ko "****"
	$(ADB) shell insmod /system/lib/modules/fpga_dotmatrix.ko
	$(ADB) shell lsmod | grep fpga_dotmatrix
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
