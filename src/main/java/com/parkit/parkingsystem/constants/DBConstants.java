package com.parkit.parkingsystem.constants;

public class DBConstants {

    public static final String GET_NEXT_PARKING_SPOT = "select min(id) from parking_spot where available = true and parking_type = ?";
    public static final String UPDATE_PARKING_SPOT = "update parking_spot set available = ? where id = ?";
    public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME) values(?,?,?,?,?)";
    public static final String UPDATE_TICKET = "update ticket set PRICE=?, OUT_TIME=? where ID=?";
    //public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? order by t.IN_TIME  limit 1";
    public static final String GET_TICKET = "select id, parking_number, vehicle_reg_number, price, in_time, out_time from ticket where parking_number = ?";
    public static final String SAVE_PARKING = "insert into parking_spot(parking_type, available) values(?,?)";
}
