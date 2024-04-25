#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/init.h>
#include <asm/uaccess.h>
#include <asm/io.h>
#include <linux/ioport.h>
#include <linux/fs.h>
#include <linux/miscdevice.h>
#include <linux/mutex.h>

#define LED_ADDRESS			0x05000020
#define LED_ADDRESS_RANGE 	0x1000

#define DEVICE_NAME			"fpga_led"

static int led_usage = 0;
static unsigned long int *led_ioremap;

static int fpga_led_open(struct inode * inode, struct file * file)
{
	if (led_usage == -1)
		return -EBUSY;

	led_ioremap = ioremap(LED_ADDRESS, LED_ADDRESS_RANGE);

	if ( check_mem_region( (unsigned long int) led_ioremap, LED_ADDRESS_RANGE) /* != 0 */)
	{
		printk(KERN_WARNING "Can't get IO Region 0x%x\n", (unsigned int) led_ioremap);
		return -1;
	}
	
	request_mem_region( (unsigned long int) led_ioremap, LED_ADDRESS_RANGE, DEVICE_NAME);
	led_usage = 1;

	return 0;
}

static int fpga_led_release(struct inode * inode, struct file * file)
{
	release_mem_region( (unsigned long int) led_ioremap, LED_ADDRESS_RANGE);
	iounmap(led_ioremap);

	led_usage = 0;
	return 0;
}

/* not use
static ssize_t fpga_led_read(struct file * file, char * buf, size_t length, loff_t * ofs)
{
	printk("fpga_led_read, \n");
	
	return 0;
}
*/

static ssize_t fpga_led_write(struct file * file, const char * buf, size_t length, loff_t * ofs)
{
	unsigned char value;

	get_user(value, buf);
	char tmp[20] = {0};
	memcpy(tmp, buf, 20);
	tmp[strcspn(tmp, "\n")] = 0;

	printk("led_write %s, \n", tmp); 
	if (strcmp(tmp, "off") == 0) {
		printk("led_write -> OFF!\n");
		*(unsigned short int *)led_ioremap = 0x00;
		}	
	else if (tmp[0] >= '1' && tmp[0] <= '9') {
        	value = tmp[0] - '1';
		printk("led_write -> NUM %d\n:", value);
        *(unsigned short int *)led_ioremap = 1 << (value - 1);
	} else {
		printk("led_write -> ALL ON");
		 *(unsigned short int *)led_ioremap = 0xFF;
	}
	
	return length;
}

/* not use
static DEFINE_MUTEX(fpga_led_mutex);
static long fpga_led_ioctl(struct file * file, unsigned int cmd, unsigned long arg){
	printk("fpga_led_ioctl, \n");
	
	switch(cmd){
		default:
			mutex_unlock(&fpga_led_mutex);
			return ENOTTY;
	}
	
	mutex_unlock(&fpga_led_mutex);
	return 0;
}
*/

static struct file_operations fpga_led_fops = {
	.owner = THIS_MODULE,
	.open = fpga_led_open,
	.release = fpga_led_release,
//	.read = fpga_led_read,
	.write = fpga_led_write,
//	.unlocked_ioctl = fpga_led_ioctl,
};

static struct miscdevice fpga_led_driver = {
	.minor = MISC_DYNAMIC_MINOR,
	.name = "fpga_led",
	.fops = &fpga_led_fops,
};

static int fpga_led_init(void){
	printk("fpga_led_init - modified driver, \n");
	
	return misc_register(&fpga_led_driver);
}

static void fpga_led_exit(void){
	printk("fpga_led_exit - modified driver, \n");

	misc_deregister(&fpga_led_driver);
	
}

module_init(fpga_led_init);
module_exit(fpga_led_exit);

MODULE_AUTHOR("Hanback");
MODULE_DESCRIPTION("fpga_led driver modified");
MODULE_LICENSE("Dual BSD/GPL");
