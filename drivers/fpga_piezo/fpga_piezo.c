#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/init.h>
#include <asm/uaccess.h>
#include <asm/io.h>
#include <linux/ioport.h>
#include <linux/fs.h>
#include <linux/miscdevice.h>
#include <linux/mutex.h>

#define PIEZO_ADDRESS			0x05000050
#define PIEZO_ADDRESS_RANGE 	0x1000

#define DEVICE_NAME			"fpga_piezo"

static int piezo_usage = 0;
static unsigned long int *piezo_ioremap;

static int fpga_piezo_open(struct inode * inode, struct file * file)
{
	if (piezo_usage == -1)
		return -EBUSY;

	piezo_ioremap = ioremap(PIEZO_ADDRESS, PIEZO_ADDRESS_RANGE);

	if ( check_mem_region( (unsigned long int) piezo_ioremap, PIEZO_ADDRESS_RANGE) /* != 0 */)
	{
		printk(KERN_WARNING "Can't get IO Region 0x%x\n", (unsigned int) piezo_ioremap);
		return -1;
	}
	
	request_mem_region( (unsigned long int) piezo_ioremap, PIEZO_ADDRESS_RANGE, DEVICE_NAME);
	piezo_usage = 1;

	return 0;
}

static int fpga_piezo_release(struct inode * inode, struct file * file)
{
	release_mem_region( (unsigned long int) piezo_ioremap, PIEZO_ADDRESS_RANGE);
	iounmap(piezo_ioremap);

	piezo_usage = 0;
	return 0;
}

/* not use
static ssize_t fpga_piezo_read(struct file * file, char * buf, size_t length, loff_t * ofs)
{
	printk("fpga_piezo_read, \n");
	
	return 0;
}
*/

static ssize_t fpga_piezo_write(struct file * file, const char * buf, size_t length, loff_t * ofs)
{
	unsigned char c;

	get_user(c, buf);

	*(unsigned short int *)piezo_ioremap = c;
	
	return length;
}

/* not use
static DEFINE_MUTEX(fpga_piezo_mutex);
static long fpga_piezo_ioctl(struct file * file, unsigned int cmd, unsigned long arg){
	printk("fpga_piezo_ioctl, \n");
	
	switch(cmd){
		default:
			mutex_unlock(&fpga_piezo_mutex);
			return ENOTTY;
	}
	
	mutex_unlock(&fpga_piezo_mutex);
	return 0;
}
*/

static struct file_operations fpga_piezo_fops = {
	.owner = THIS_MODULE,
	.open = fpga_piezo_open,
	.release = fpga_piezo_release,
//	.read = fpga_piezo_read,
	.write = fpga_piezo_write,
//	.unlocked_ioctl = fpga_piezo_ioctl,
};

static struct miscdevice fpga_piezo_driver = {
	.minor = MISC_DYNAMIC_MINOR,
	.name = "fpga_piezo",
	.fops = &fpga_piezo_fops,
};

static int fpga_piezo_init(void){
	printk("fpga_piezo_init, \n");
	
	return misc_register(&fpga_piezo_driver);
}

static void fpga_piezo_exit(void){
	printk("fpga_piezo_exit, \n");

	misc_deregister(&fpga_piezo_driver);
	
}

module_init(fpga_piezo_init);
module_exit(fpga_piezo_exit);

MODULE_AUTHOR("Hanback");
MODULE_DESCRIPTION("fpga_piezo driver test");
MODULE_LICENSE("Dual BSD/GPL");
