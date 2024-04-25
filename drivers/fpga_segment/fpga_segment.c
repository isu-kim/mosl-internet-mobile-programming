#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/init.h>
#include <asm/uaccess.h>
#include <asm/io.h>
#include <linux/ioport.h>
#include <linux/fs.h>
#include <linux/miscdevice.h>
#include <linux/mutex.h>
#include <linux/delay.h>

#define SEGMENT_ADDRESS_GRID 0x05000030
#define SEGMENT_ADDRESS_DATA 0x05000032
#define SEGMENT_ADDRESS_RANGE 4

static int segmentUsage = 0;
static unsigned long int *segment_grid;
static unsigned long int *segment_data;

static int fpga_segment_open(struct inode * inode, struct file * file)
{
	if (segmentUsage == 1)
		return -EBUSY;
	segment_grid = ioremap(SEGMENT_ADDRESS_GRID, SEGMENT_ADDRESS_RANGE);
	segment_data = ioremap(SEGMENT_ADDRESS_DATA, SEGMENT_ADDRESS_RANGE);

	if (check_mem_region( (unsigned long int)segment_grid, SEGMENT_ADDRESS_RANGE) != 0
						|| check_mem_region( (unsigned long int)segment_data, SEGMENT_ADDRESS_RANGE) != 0)
	{
		printk(KERN_WARNING"unable to register\n");
		return -EBUSY;
	}

	request_region( (unsigned long int)segment_grid, SEGMENT_ADDRESS_RANGE, "segment");
	request_region( (unsigned long int)segment_data, SEGMENT_ADDRESS_RANGE, "segment");

	segmentUsage = 1;
	return 0;
}

static int fpga_segment_release(struct inode * inode, struct file * file)
{
	release_region( (unsigned long int)segment_grid, SEGMENT_ADDRESS_RANGE);
	release_region( (unsigned long int)segment_data, SEGMENT_ADDRESS_RANGE);

	iounmap(segment_grid);
	iounmap(segment_data);

	segmentUsage = 0;
	return 0;
}

/* not use
static ssize_t fpga_segment_read(struct file * file, char * buf, size_t length, loff_t * ofs){
	printk("fpga_segment_read, \n");
	
	return 0;
}
*/

static int getSegmentCode(int num)
{
	static int codes[] = {0xFC, 0x60, 0xDA, 0xF2, 0x66, 0xB6, 0xBE, 0xE4, 0xFE, 0xF6,
				   0xFA, 0x3E, 0x1A, 0X7A, 0X9E, 0x8E};
	return codes[num];
}

static ssize_t fpga_segment_write(struct file * file, const char * buf, size_t length, loff_t * ofs)
{
	static int digit[6] = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20};
	int i;
	char nums[7];

	copy_from_user(nums, buf, length);

	for(i=0; i<6; ++i)
	{
		*segment_grid = digit[i];
		*segment_data = getSegmentCode(nums[i] - '0');
		mdelay(1);
	}
	
	return length;
}

/* not use
static DEFINE_MUTEX(fpga_segment_mutex);
static long fpga_segment_ioctl(struct file * file, unsigned int cmd, unsigned long arg){
	printk("fpga_segment_ioctl, \n");
	
	switch(cmd){
		default:
			mutex_unlock(&fpga_segment_mutex);
			return ENOTTY;
	}
	
	mutex_unlock(&fpga_segment_mutex);
	return 0;
}
*/

static struct file_operations fpga_segment_fops = {
	.owner = THIS_MODULE,
	.open = fpga_segment_open,
	.release = fpga_segment_release,
//	.read = fpga_segment_read,
	.write = fpga_segment_write,
//	.unlocked_ioctl = fpga_segment_ioctl,
};

static struct miscdevice fpga_segment_driver = {
	.minor = MISC_DYNAMIC_MINOR,
	.name = "fpga_segment",
	.fops = &fpga_segment_fops,
};

static int fpga_segment_init(void){
	printk("fpga_segment_init, \n");
	
	return misc_register(&fpga_segment_driver);
}

static void fpga_segment_exit(void){
	printk("fpga_segment_exit, \n");

	misc_deregister(&fpga_segment_driver);
	
}

module_init(fpga_segment_init);
module_exit(fpga_segment_exit);

MODULE_AUTHOR("Hanback");
MODULE_DESCRIPTION("fpga_segment driver test");
MODULE_LICENSE("Dual BSD/GPL");
