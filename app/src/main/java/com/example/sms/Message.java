package com.example.sms;

public class Message {
    private int Id;
    private String Name;
    private String PhoneNo;
    private String Body;

    public Message(){}
    public Message(int id, String name, String phoneNo, String body) {
        Id = id;
        Name = name;
        PhoneNo = phoneNo;
        Body = body;
    }

    public int getId() {return Id;}
    public void setId(int id) {Id = id;}

    public String getName() { return Name; }
    public void setName(String name) { Name = name; }

    public String getPhoneNo() {return PhoneNo;}
    public void setPhoneNo(String phoneNo) {PhoneNo = phoneNo;}

    public String getBody() { return Body; }
    public void setBody(String body) {Body = body; }
}
