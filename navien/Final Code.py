#!/usr/bin/env python
# coding: utf-8

# In[15]:


import pandas as pd
import time
import numpy as np
from pandas import ExcelFile


# In[16]:


file_loc = '/Users/keeyou/Desktop/Navien/4-2020 Sales Commission (to be detailed).xlsx'


# In[28]:


#Find Collections and SAP to make DataFrame and then, return three DataFrames
def returnDataFrame(fileLoc):
    read_summary = pd.read_excel(file_loc, sheet_name = 'Total by Rep (USD Funds)')
    read_detail = pd.read_excel(file_loc, sheet_name = 'SAP')
    read_new = pd.read_excel(file_loc, sheet_name = 'New Product')
    summary = pd.DataFrame(read_summary)
    detail = pd.DataFrame(read_detail)
    newProduct = pd.DataFrame(read_new)
    return summary, detail, newProduct


# In[29]:


startTime = time.time()
summary, detail, newProduct = returnDataFrame(file_loc)
print("--- %s seconds ---" % (time.time() - startTime))


# In[56]:


#Sort billing numbers in increasing order in detail (SAP)
detail = detail.sort_values(by=['Billing Doc.'])
#Reset index 
detail = detail.reset_index()


# In[57]:


#If the billing number does not start with 9 or the length of it is not equal to 8 then remove those rows.
def filteringBillingNumber(summary, table):
    for i in summary.index:
        billingNum = str(summary['Invoice / Credit Memo'][i])
        if len(billingNum) != 8 and billingNum[0] != "9":
            table = table.append(summary.iloc[i, :])
            summary = summary.drop([i], axis=0)
    summary = summary.reset_index()
    return summary, table


# In[58]:


#Find rows corresponding to its billing numbers from detailed (SAP)
def findPosition(detail, billingNum):
    start = 0
    end = detail.index._stop-1
    middle = (end//2)
    #print(start, middle, end)
    count = 0
    while (billingNum != str(detail['Billing Doc.'][start]) 
           and billingNum != str(detail['Billing Doc.'][middle])
           and billingNum != str(detail['Billing Doc.'][end]) 
           and count < 15):
        if billingNum > str(detail['Billing Doc.'][start]) and billingNum < str(detail['Billing Doc.'][middle]):
            end = middle
            middle = ((end + start)//2)
            #print(1)
            #print(start, middle, end)
        else:
            start = middle
            middle = ((end + start)//2)
            #print(2)
            #print(start, middle, end)
        count += 1
    return start, end


# In[60]:


def returnRows(billingNum, Type, detail, percentDiscount, amount, discountAmt, rate, ck, depositDate, salesRep):
    total = 0
    count = 0
    totalDiscount = 0
    #Match Type with PH1 D.
    ph1d = ""
    if Type == "WH":
        ph1d = "US_WATER HEATER"
    elif Type == "Boiler":
        ph1d = "US_BOILER"
    elif Type == "Part":
        ph1d = "US_OTHER ITEMS"
     
    rows = pd.DataFrame([], columns=[])
    start, end = findPosition(detail, str(billingNum))
    for i in range(start, end):
        if str(detail['Billing Doc.'][i]) == str(billingNum) and detail['PH1 D.'][i] == ph1d:
            discountedPrice = float(detail['Net amount'][i] - (float(detail['Net amount'][i]) * float(percentDiscount)))
            if pd.isna(percentDiscount):
                discountedPrice = round(float(detail['Net amount'][i]), 2)
            total += detail['Net amount'][i]
            oldOrNew = checkOldNew(detail['Material1'][i], [detail['Material'][i]], newProduct)
            extraCom = 0.0
            if oldOrNew == "New":
                #Extra Commission: 0.005 = 0.5%
                extraCom = round(discountedPrice * 0.005, 2)
            df = pd.DataFrame({'Deposit Date': [depositDate],
                               'Ck#': [ck],
                               'P.O.# / RGA# / Debit Memo #': [detail['PO NO.'][i]], 
                               'Invoice / Credit Memo': [detail['Billing Doc.'][i]], 
                               'Customer': [detail['Sold-to'][i]],
                               'City': [detail['Ship-to city Name'][i]],
                               'State / Prov.': [detail['Ship-to state'][i]],
                               'Sales Rep': [salesRep],#[detail['Sales Rep(Doc)1'][i]],
                               'New/ Old': [oldOrNew],
                               'Item': [detail['Material'][i]], 
                               'Item Desc': [detail['Material1'][i]],
                               'Receipt Amt': [round(float(detail['Total amount'][i]), 2)],
                               'Sales Discount': [round(float(detail['Net amount'][i]) * float(percentDiscount), 2)],
                               'Net Amt': [round(discountedPrice, 2)],
                               'Commission': [round(discountedPrice * rate, 2)],
                               'Extra Comm': [extraCom],
                               'Rate': [rate],
                               'Type': [Type]},#[detail['PH1 D.'][i]]}, 
                               index = [count])
            totalDiscount += float(detail['Net amount'][i]) * float(percentDiscount)
            rows = rows.append(df)
            count += 1
            
    #Set true and false to check if the total amount matches
    accuracy = False
    if round(float(total), 2) == round(float(amount), 2):
        accuracy = True
    accuracyDis = False
    if round(float(totalDiscount), 2) == round(float(discountAmt), 2):
        accuracyDis = True
    if pd.isna(totalDiscount):
        accuracyDis = True
    #Add totalAmount at the end of row
    totalAmount = pd.DataFrame({'Item Desc': ['Total'], 
                                'Receipt Amt': [round(total, 2)], 
                                'Accuracy of Receipt Amt': [str(accuracy)],
                                'Sales Discount': [round(totalDiscount, 2)],
                                'Accuracy of Sales Discount': [str(accuracyDis)]})

    #Append totalAmount row
    rows = rows.append(totalAmount, ignore_index=True)
    return rows


# In[61]:


#Check if the product is new or old
def checkOldNew(itemNum, itemDesc, newProduct):
    oldOrNew = "Old"
    for i in newProduct.index:
        if newProduct['Product Number'][i] == itemNum or newProduct['Product Description'][i] == itemDesc:
            oldOrNew = "New"
    return oldOrNew


# In[62]:


#HIghlight the summarized row with yellow color
def highlight(row):
    if pd.isna(row.Customer):
        return ['background-color: white'] * len(row.values)
    if pd.isna(row.Item):
        return ['background-color: yellow'] * len(row.values)
    else:
        return ['background-color: white'] * len(row.values)


# In[63]:


#summary = summary[summary.Ck != 'Transfer']
startTime = time.time()
table = pd.DataFrame([], columns=['Deposit Date', 'Ck#', 'P.O.# / RGA# / Debit Memo #', 'Invoice / Credit Memo', 
                                  'Customer', 'City', 'State / Prov.', 'Sales Rep', 'New/ Old', 'Item',
                                  'Item Desc', 'Receipt Amt', 'Accuracy of Receipt Amt', 'Sales Discount', 
                                  'Accuracy of Sales Discount', 'Shipping', 'DC/120+ Discount', 'Adj.', 
                                  'Net Amt', 'Commission', 'Rate', 'Extra Comm', 'Notes', 'Type'])
for i in summary.index:
    print(round(i/summary.index._stop*100, 2), "%")
    #End the program when it reaches the end
    if pd.isna(summary['Ck#'][i]):
        break;
    table = table.append(summary.iloc[i, :])
    percentDiscount = summary['Sales Discount'][i] / summary['Receipt Amt'][i]
    amount = float(summary['Receipt Amt'][i])
    amountDis = float(summary['Sales Discount'][i])
    rate = float(summary['Rate'][i])
    ck = summary['Ck#'][i]
    Type = summary['Type'][i]
    depositDate = summary['Deposit Date'][i]
    salesRep = summary['Sales Rep'][i]
    rows = returnRows(summary['Invoice / Credit Memo'][i], Type, detail, percentDiscount, amount, amountDis, rate, ck, depositDate, salesRep)
    
    for j in rows.index:
        table = table.append(rows.iloc[j, :])
table = table.reset_index(drop=True).style.apply(highlight, axis=1)
print("100%")
print("--- %s seconds ---" % (time.time() - startTime))
print("Finished")


# In[65]:


startTime = time.time()
table.to_excel("04-2020 (USD Funds).xlsx")
print("--- %s seconds ---" % (time.time() - startTime))


# In[ ]:




