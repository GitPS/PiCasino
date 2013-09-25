package com.piindustries.picasino;

/**
 * Date: 9/20/13
 * Time: 1:18 PM
 */

public class PiCasino {

    public static void main(String[] args){

       /* JFrame j = new JFrame("This is a test");
        j.setBounds(100,100,500,500);
        j.setVisible(true);
        j.setLayout(null);

        JTextArea jp = new JTextArea();
        jp.setBounds(10,10,480,480);
        j.add(jp);
        j.repaint();  */

        System.out.println("This is a test line to let you compile and see that it is working!");
        System.out.println("Lets have our scrum meetings via println()'s.\r\n" );
        System.out.println("That sound good to me!\r\n");
            for(int i = 0; i < 10000; i++){
                System.out.println(i+"\r\n");
                    try {
                        Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

}
