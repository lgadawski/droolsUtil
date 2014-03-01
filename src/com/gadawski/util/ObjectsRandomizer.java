package com.gadawski.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Random;

import com.gadawski.util.facts.Car;
import com.gadawski.util.facts.Customer;
import com.gadawski.util.facts.House;

/**
 * Utility class responsible for generating random objects to file.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
public class ObjectsRandomizer {
    /**
     * Coding for output file.
     */
    public static final String sTEXT_CODING = "utf-8";
    /**
     * Definition of line separator in output file. -> \n
     */
    public static final String sLINE_SEPARATOR = System
            .getProperty("line.separator");
    /**
     * Number of random generated person objects in file.
     */
    private static int sNUM_OF_CUSTOMERS = 100;
    /**
     * Number of random generated car objects in file.
     */
    private static int sNUM_OF_CARS = 100;
    /**
     * Number of random generated house objects in file.
     */
    private static int sNUM_OF_HOUSES = 100;
    /**
     * 
     */
    private File file;

    public ObjectsRandomizer(String path) throws FileNotFoundException {
        file = new File(path);
    }

    /**
     * Generates data to file.
     * 
     * @param generatedDataFilename
     * 
     * @param path
     *            - file name for output data.
     * @param noHouses
     * @param noCars
     * @param noCustomers
     * @throws FileNotFoundException
     */
    public void generateData(Integer noCustomers, Integer noCars,
            Integer noHouses) throws FileNotFoundException {
        Writer writer = null;
        sNUM_OF_CUSTOMERS = noCustomers;
        sNUM_OF_CARS = noCars;
        sNUM_OF_HOUSES = noHouses;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), sTEXT_CODING));
            writeToFile(writer);
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * For a give writer, writes random objects.
     * 
     * @param writer
     *            - writer to write :)
     * @throws IOException
     *             - throws if I/O exception.
     */
    private void writeToFile(final Writer writer) throws IOException {
        writeCustomers(writer);
        writeCars(writer);
        writeHouses(writer);
    }

    /**
     * Writes lines with random car attributes.
     * 
     * @param writer
     *            - writer to write car..
     * @throws IOException
     */
    private void writeCars(final Writer writer) throws IOException {
        final Random random = new Random();
        for (int i = 0; i < sNUM_OF_CARS; i++) {
            final int name = i;
            final BigDecimal price = randomBigDecimal(random, Car.sMIN_PRICE,
                    Car.sMAX_PRICE);
            writer.write("(car (name " + name + ") (price " + price + ") )"
                    + sLINE_SEPARATOR);
        }
    }

    /**
     * Writes lines with random houses attributes.
     * 
     * @param writer
     * @throws IOException
     */
    private void writeHouses(final Writer writer) throws IOException {
        final Random random = new Random();
        for (int i = 0; i < sNUM_OF_HOUSES; i++) {
            final int name = i;
            final BigDecimal price = randomBigDecimal(random, House.sMIN_PRICE,
                    House.sMAX_PRICE);
            final BigDecimal area = randomBigDecimal(random, House.sMIN_AREA,
                    House.sMAX_AREA);
            writer.write("(house (name " + name + ") (price " + price
                    + ") (area " + area + ") )" + sLINE_SEPARATOR);
        }
    }

    /**
     * Writes lines with random person attributes.
     * 
     * @param writer
     *            - writer to write a people..
     * @throws IOException
     */
    private void writeCustomers(final Writer writer) throws IOException {
        final Random random = new Random();
        for (int i = 0; i < sNUM_OF_CUSTOMERS; i++) {
            final int name = i;
            final int age = randomInt(random, Customer.sMIN_AGE,
                    Customer.sMAX_AGE);
            final BigDecimal income = randomBigDecimal(random,
                    Customer.sMIN_INCOME, Customer.sMAX_INCOME);
            final BigDecimal cash = randomBigDecimal(random,
                    Customer.sMIN_CASH, Customer.sMAX_CASH);
            final boolean married = random.nextBoolean();
            writer.write("(customer (name " + name + ") (age " + age
                    + ") (income " + income + ") (cash " + cash + ") (married "
                    + married + ") )" + sLINE_SEPARATOR);
        }
    }

    /**
     * Creates random in in givem range.
     * 
     * @param random
     *            - to get random integer.
     * @param min
     *            - min value for int.
     * @param max
     *            - max value for int.
     * @return
     */
    public static int randomInt(final Random random, final int min,
            final int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    /**
     * TODO change for proper randoming BigDecimal number if necessary!
     * 
     * Creates random BigDecimal object with .xx precision based on random float
     * number.
     * 
     * @param random
     *            - to get random float.
     * @param min
     *            - min value for float.
     * @param max
     *            - max value for float.
     * @return
     */
    public static BigDecimal randomBigDecimal(final Random random,
            final float min, final float max) {
        return BigDecimal.valueOf(random.nextFloat() * (max - min) + min)
                .setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
