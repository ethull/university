import csv

testNames = [
    "SMA_X>SMA_Y",
    "EMA_X>SMA_X",
    "TBR_X==0",
    "ABS(TBR_X)>VOL_X",
    "MOM_X>0",
    "MOM_X>20",
]

inputs = [[],[],[],[],[],[],]

results = {
    "sma_x": [],
    "ema_x": [],
    "tbr_x": [],
    "vol_x": [],
    "mom_x": [],
    "sma_y": [],
}

prices = []
priceIncreases = []
numDays = 0
numNans = 24 # X = 12, Y = 24

# load price of the csv's
def loadResults():
    with open('PriceResults.csv', newline='') as csvfile:
        spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
        a = 0
        for row in spamreader:
            #  skip the header row
            if row[0] == "Price": continue
            # skip nans for first couple of days
            a+=1
            if a < numNans: continue
            # load results into dict
            prices.append(float(row[0]))
            results["sma_x"].append(float(row[1]))
            results["ema_x"].append(float(row[2]))
            results["tbr_x"].append(float(row[3]))
            results["vol_x"].append(float(row[4]))
            results["mom_x"].append(float(row[5]))
            results["sma_y"].append(float(row[6]))
            priceIncreases.append(row[7])
        global numDays
        numDays = len(prices)
        # print(results)
        # print(prices)
        # print(priceIncreases)

# run all the tests and write to dict
def runTests():
    for i in range(numDays):
        # test1, sma_x > sma_y
        # print("{} > {}?: {}".format(results["sma_x"][i], results["sma_y"][i], results["sma_x"][i] > results["sma_y"][i]))
        if results["sma_x"][i] > results["sma_y"][i]:
            inputs[0].append(1)
        else:
            inputs[0].append(0)
        # test2, ema_x > sma_x
        if results["ema_x"][i] > results["sma_x"][i]:
            inputs[1].append(1)
        else:
            inputs[1].append(0)
        # test3, tbr_x == 0
        if results["tbr_x"][i] == 0.0:
            inputs[2].append(1)
        else:
            inputs[2].append(0)
        # test4, abs(tbr_x) > vol_x
        if abs(results["tbr_x"][i]) > results["vol_x"][i]:
            inputs[3].append(1)
        else:
            inputs[3].append(0)
        # test5, mom_x>0
        if results["mom_x"][i] > 0.0:
            inputs[4].append(1)
        else:
            inputs[4].append(0)
        # test6, mom_x>20
        if results["mom_x"][i] > 20:
            inputs[5].append(1)
        else:
            inputs[5].append(0)

# write the test results as input for the GA
def writeInputs():
    with open('GAInputs.csv', 'w', newline='') as csvfile:
        spamwriter = csv.writer(csvfile, delimiter=',',
                                quotechar='|', quoting=csv.QUOTE_MINIMAL)
        spamwriter.writerow([testNames[0],testNames[1],testNames[2],testNames[3],testNames[4],testNames[5],"Increase in 14 days"])
        for i in range(len(prices)):
            spamwriter.writerow([inputs[0][i], inputs[1][i], inputs[2][i], inputs[3][i], inputs[4][i], inputs[5][i], priceIncreases[i]])

def main():
    loadResults()
    runTests()
    writeInputs()

main()
