# omnitor-android

Omnitor for Android is the open-source version of one of my past projects. The app records the user's mobile phone consumption--sent and received SMS, incoming and outgoing calls, bytes of data sent and received via mobile internet and WiFi/LAN, and the network roaming state as these transactions occur. These logs are uploaded to a server on a regular schedule, and are used by the client to recommend to the user the best mobile plan available on a local telco.

### First run

On first launch, the app generates a UUID to uniquely identify the installation. It then remembers information such as the time of first launch, the current network roaming state, and the current values of Android's built-in data counters, if supported in the device.

Then, a background thread is fired to upload device information in the below JSON format. If the upload fails, Ominitor attempts again in the next minute.

```javascript
{
    "uuid" : (string),
    "type" : "device", // type of log
    "model" : (string),
    "manufacturer" : (string),
    "first_run" : (long) // time when first run
}
```

Finally, alarms are set for regularly scheduled tasks: the outgoing SMS logger (<code>OutSmsLogger</code>), the data consumption logger (<code>DataLogger</code>), and the <code>Uploader</code>. For this project, those three tasks are scheduled to be fired every 1 minute.

The app doesn't do anything special whenever it is launched after the first time.

### Sign up

If the user decides to sign up, Omnitor simply uploads the user's email together with the app's UUID.

```javascript
{
    "uuid" : (string),
    "type" : "user",
    "email" : (string)
}
```

If the upload fails, the app simply tries again the next minute. If successful, whenever the app is launched, a screen with the following message will always show up:

```
Hello! Don't mind us--we're working in the background. Go do your stuff as usual.
```

### Incoming SMS

Whenever a text message arrives, <code>InSmsLogger</code> is fired and the following information about the SMS is stored in SQLite.

```javascript
{
    "uuid" : (string),
    "type" : "in_sms",
    "number" : (string), // the sender
    "sim_number" : (string), // the user's mobile phone number
    "time" : (long),
    "length" : (int), // number of characters in the SMS
    "roaming" : (boolean) // network roaming state upon receive
}
```

### Outgoing SMS

<code>OutSmsLogger</code> is fired on a set schedule because there's currently no public API in Android to monitor outgoing SMSes as they are sent. The logger looks at the SMS messages that are sent and logs the following info.

```javascript
{
    "uuid" : (string),
    "type" : "out_sms",
    "number" : (string), // the recipient
    "sim_number" : (string), // user's number
    "time" : (long),
    "length" : (int), // number of characters in the SMS
    "roaming" : (boolean) // network roaming state upon send
}
```

If the user deletes all the messages before the logger is fired, then it will record nothing.

### Incoming calls

After an incoming call, <code>InCallLogger</code> fires a background thread that records the following information about the call.

```javascript
{
    "uuid" : (string),
    "type" : "in_call",
    "number" : (string), // the caller
    "sim_number" : (string), // user's number
    "time_started" : (long),
    "time_answered" : (long),
    "time_ended" : (long),
    "roaming" : (boolean)
}
```

### Outgoing calls

After an outgoing call, <code>OutCallLogger</code> fires a background thread that records similar information as with incoming calls, except for <code>time_answered</code>. There currently are no public APIs in Android to detect when the other line picks up the call our user is making.

```javascript
{
    "uuid" : (string),
    "type" : "out_call",
    "number" : (string), // the target number
    "sim_number" : (string), // user's number
    "time_started" : (long),
    "time_ended" : (long),
    "roaming" : (boolean)
}
```

### Data consumption

Omnitor distinguishes between data consumption over WiFi/LAN and over mobile internet (3G, 4G). As mentioned earlier, the values of Android's built-in data counters (in bytes) are remembered upon the app's first launch. Since <code>DataLogger</code> is fired every minute, it simply subtracts the new values of those counters and creates a log in the format below.

```javascript
{
    "uuid" : (string),
    "type" : "data",
    "network_sent" : (long), // bytes sent, WiFi/LAN
    "network_received" : (long), // bytes received, WiFi/LAN
    "mobile_sent" : (long), // bytes sent, mobile data
    "mobile_received" : (long), // bytes received, mobile data
    "roaming" : (boolean)
}
```

### Roaming

Caution: This is the only part of the code that is based on StackOverflow research and is left UNTESTED. Network roaming occurs when the phone goes through third-party telco services to perform a transaction, and is therefore only possible to test by going out of the country.

A <code>BroadcastReceiver</code> called <code>GeneralBroadcastReceiver</code> has been set up to receive "android.net.conn.CONNECTIVITY_CHANGED" together with the other tasks that Omnitor fires.

```xml
<receiver android:name=".GeneralBroadcastReceiver">
    <intent-filter>
        <!-- other intent actions here -->
        <action android:name="android.net.conn.CONNECTIVITY_CHANGED" />
    </intent-filter>
</receiver>
```

Since the old network roaming state was remembered during the app's first launch, Omnitor simply gets the current roaming state when the above intent is fired. <code>GeneralBroadcastReceiver</code> then compares the two states and if they are not equal, <code>OutSmsLogger</code> and <code>DataLogger</code> are fired again to contain the *old* roaming state. This is because they are the only two loggers who are not able to record events as they happen in real-time. It is expected that from this point onward, any further triggers of <code>OutSmsLogger</code> and <code>DataLogger</code> will contain the *new* roaming state.

### Uploading

The <code>Uploader</code>, as its name implies, performs the task of uploading. For Omnitor, the database is hosted in [MongoLab](http://mongolab.com) and accessed via their REST interface. Whenever fired, <code>Uploader</code> simply transforms all the logs in SQLite into a single, polymorphic JSON array and sends it to the server. If successful, the SQLite database is cleared. If not, the logs will be uploaded together with the new ones in the next schedule.

### Thread collisions

Note that the above tasks may be fired concurrently and arbitrarily. Synchronous access to the SQLite database is properly handled. However, since it's possible for the <code>Uploader</code> to finish before the other loggers, logs created after the upload will be uploaded in the next scheduled alarm.