package com.flypgs.pages;

/**
 * Created by Dmytro Novikov on 4/3/2015.
 */
public class Helpers {

/*
TestSuit.java - creates monthes
    @Test
    public void testCase0000(){
        HomePage hp = new HomePage(driver);
        hp.get();
        List<String> languages = hp.getLanguages();
        for(String language : languages){
            hp.setCurrentLanguage(language);
            assertTrue("Can't set " + language + " as a site's language.",
                    hp.getCurrentLanguage().contains(language));
            driver.findElement(By.cssSelector("input[id='linked-gidis']")).click();
            for(int i = 0; i < 9; i++)
                driver.findElement(By.xpath("./*/
/*[@id='ui-datepicker-div']/div/a[2]/span")).click();
            System.out.print("\"" + language +"\"");
            for(int i = 0; i < 11; i++){
                System.out.print(", \"" + driver.findElement(By.className("ui-datepicker-month")).getText() + "\"");
                driver.findElement(By.xpath("./*/
/*[@id='ui-datepicker-div']/div/a[2]/span")).click();
            }
            System.out.println();
            //break;
        }
    }
*/

/*
TestSuit.java - creates connected_cities_en.csv
        try {
            BufferedWriter dataFile = new BufferedWriter(
                    new FileWriter(new File("C:\\Users\\Dmytro\\Documents\\QA\\connected_cities_en.csv")));
            for(String city : ott.getDepCities().keySet()){
                ott.setCityFrom(city);
                java.util.Set<String> done = null;
                while(done == null){
                    try{
                        done = ott.getDestCities().keySet();;
                    } catch(java.lang.NullPointerException e){
                        //
                    }
                }
                for(String city1 : done){
                    dataFile.write(city);
                    dataFile.write(", ");
                    dataFile.write(city1);
                    dataFile.newLine();
                }
                //break;
            }
            dataFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
}
