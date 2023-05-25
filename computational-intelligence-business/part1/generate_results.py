import csv
# import talib
# import numpy
from talib.abstract import (SMA, EMA, MOM, STDDEV) #  VOL, TBR
import numpy as np

np.set_printoptions(threshold=np.inf)
# all the stocks prices
prices = []
# all the stocks prices as a numpy array
pricesNp = None
# all the stock price increases
priceIncreases = []
# results dictionary of average calcs
results = {
    "sma_x": None,
    "ema_x": None,
    "tbr_x": None,
    "vol_x": None,
    "mom_x": None,
    "sma_y": None,
}

# load price of the csv's
def loadPrice():
    with open('PriceData.csv', newline='') as csvfile:
        spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
        for row in spamreader:
            prices.append(float(row[0]))
            priceIncreases.append(row[1])
            # print(', '.join(row))
        # print(prices)
        # print(priceIncreases)
        # print(len(prices))
        # print(len(priceIncreases))
        # convert prices to nparray
        global pricesNp
        pricesNp = np.array(prices)

def calcAverages():
    # pricesNp = np.array(prices[0:5])
    # print("pricesNp: {}".format(pricesNp))
    inputs = {
        'close': pricesNp
    }
    results["sma_x"] = SMA(inputs, 12)
    results["ema_x"] = EMA(inputs, 12)
    results["tbr_x"] = TBR(12)
    results["vol_x"] = STDDEV(inputs, 12)/SMA(inputs, 12)
    results["mom_x"] = MOM(inputs, 12)
    results["sma_y"] = SMA(inputs, 24)

def printResults():
    print("results:")
    # print("mom: {}".format(results["mom_x"]))

# write results to csv
def writeResults():
    with open('PriceResults.csv', 'w', newline='') as csvfile:
        spamwriter = csv.writer(csvfile, delimiter=',',
                                quotechar='|', quoting=csv.QUOTE_MINIMAL)
        spamwriter.writerow(["Price","SMA_X","EMA_X","TBR_X","VOL_X","MOM_X","SMA_Y","Increase in 14 days"])
        for i in range(len(prices)):
            spamwriter.writerow([prices[i], results["sma_x"][i], results["ema_x"][i], results["tbr_x"][i], results["vol_x"][i], results["mom_x"][i], results["sma_y"][i], priceIncreases[i]])

# cant find any options for this in TA-Lib, so done it manually
def TBR(length):
    results = []
    # index for the current period, EG: 12, 24, 36, ...
    for day in range(len(prices)):
        # if its not been length days yet, return nan
        if (day < length): results.append("nan")
        # else calc tbr
        else:
            pt = prices[day]
            currentMax = pt
            # get of max of the last "length" stocks
            for i in range(length):
                # if price day before was higher set it to max
                if (prices[day-i-1] > currentMax): currentMax = prices[day-i-1]
            tbr = (pt - currentMax) / currentMax
            results.append(tbr)
    return results;

# main func
def main():
    loadPrice()
    calcAverages()
    printResults()
    writeResults()

main()
