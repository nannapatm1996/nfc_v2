# NFC_Hotseat

Seat Booking application Trigger with NFC
 
**Data Model**

1. User: Model on property of users (index, tagid, firstname, lastname, seatname)
2. Seat: Model on property of seats (seatName, availability)

**Main Activity**
1. enable NFC Reader Mode 
2. Read of Firebase to query for found tags 
   - If tag found then it will go to "SeatBookingActivity" 
   - If tag not found then it will go to "Register Page"
4. Show tagid on UI

**NFCCardReader**

***Functions***

_1. onTagDiscover_
   - Disover tag and display onto MainActivty
   - 
_2. bytetoHexString_ 
   - convert to hex number

**SeatBooking Activity**

***Logic***
1. Choose the Seat (Toggle Imageview and keep state)
2. Click confirm to choose seats

***Main Body***
1. OnClicklistener for ImageView (Toggle 0/1) 
2. Confirm Button 
   - Check conditions 
     - if(seat1 and seat2) == state(1) --> AlertDialogBox to say not allow 
     - if(no seat chosen) --> AlertDialogBox to choose a seat 
3. Correct Usage will show Alert Message of the confirmed seat
   - call function: onSelectSeat 
   - call function: onSeatSelectUser
   - call function: SignalPhone 
   - Endprocess

**Functions**
1. SignalPhone
   - Volley a POST Request to specified URL

2. onSeatSelectUser 
   - Update SeatNumber on to User's Profile

3. onSeatSelectSeat
   - update availabilty of the seat

4. ReadSeatFirebase
   - get value of the seat from Firebase Database
   - Map with availability with Seat to show on the seasting UI
