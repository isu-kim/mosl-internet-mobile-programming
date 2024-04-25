#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/init.h>
#include <asm/uaccess.h>
#include <asm/io.h>
#include <linux/ioport.h>
#include <linux/fs.h>
#include <linux/miscdevice.h>
#include <linux/mutex.h>

#define DIPSW_ADDRESS			0x05000062
#define DIPSW_ADDRESS_RANGE 	0x1000

#define DEVICE_NAME			"fpga_dipsw"

static int dipsw_usage = 0;
static unsigned long int *dipsw_ioremap;

static int fpga_dipsw_open(struct inode * inode, struct file * file)
{
	if (dipsw_usage == -1)
		return -EBUSY;

	dipsw_ioremap = ioremap(DIPSW_ADDRESS, DIPSW_ADDRESS_RANGE);

	if ( check_mem_region( (unsigned long int) dipsw_ioremap, DIPSW_ADDRESS_RANGE) /* != 0 */)
	{
		printk(KERN_WARNING "Can't get IO Region 0x%x\n", (unsigned int) dipsw_ioremap);
		return -1;
	}
	
	request_mem_region( (unsigned long int) dipsw_ioremap, DIPSW_ADDRESS_RANGE, DEVICE_NAME);
	dipsw_usage = 1;

	return 0;
}

static int fpga_dipsw_release(struct inode * inode, struct file * file)
{
	release_mem_region( (unsigned long int) dipsw_ioremap, DIPSW_ADDRESS_RANGE);
	iounmap(dipsw_ioremap);

	dipsw_usage = 0;
	return 0;
}

static ssize_t fpga_dipsw_read(struct file * file, char * buf, size_t length, loff_t * ofs)
{
	int re;
	unsigned short int value;
	unsigned short int *dipaddr;

	dipaddr = (unsigned short int *)dipsw_ioremap;

	value = *dipaddr;

	re = copy_to_user(buf, &value, 2);
	
	return re;
}

/* not use
static ssize_t fpga_dipsw_write(struct file * file, const char * buf, size_t length, loff_t * ofs){
	printk("fpga_dipsw_write, \n");
	
	return 0;
}
*/

/* not use
static DEFINE_MUTEX(fpga_dipsw_mutex);
static long fpga_dipsw_ioctl(struct file * file, unsigned int cmd, unsigned long arg){
	printk("fpga_dipsw_ioctl, \n");
	
	switch(cmd){
		default:
			mutex_unlock(&fpga_dipsw_mutex);
			return ENOTTY;
	}
	
	mutex_unlock(&fpga_dipsw_mutex);
	return 0;
}
*/

static struct file_operations fpga_dipsw_fops = {
	.owner = THIS_MODULE,
	.open = fpga_dipsw_open,
	.release = fpga_dipsw_release,
	.read = fpga_dipsw_read,
//	.write = fpga_dipsw_write,
//	.unlocked_ioctl = fpga_dipsw_ioctl,
};

static struct miscdevice fpga_dipsw_driver = {
	.minor = MISC_DYNAMIC_MINOR,
	.name = "fpga_dipsw",
	.fops = &fpga_dipsw_fops,
};

static int fpga_dipsw_init(void){
	printk("fpga_dipsw_init, \n");
	
	return misc_register(&fpga_dipsw_driver);
}

static void fpga_dipsw_exit(void){
	printk("fpga_dipsw_exit, \n");

	misc_deregister(&fpga_dipsw_driver);
	
}

module_init(fpga_dipsw_init);
module_exit(fpga_dipsw_exit);

MODULE_AUTHOR("Hanback");
MODULE_DESCRIPTION("fpga_dipsw driver test");
MODULE_LICENSE("Dual BSD/GPL");
