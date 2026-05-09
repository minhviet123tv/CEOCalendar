package com.mvdragon.weekcalendar;

import com.mvdragon.weekcalendar.database.UserUltils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/*
Định dạng thời gian: Ngày tháng, năm, giờ ...
MM: Ngày bằng số | mm: Ngày bằng chữ tiếng Anh
HH: 24h | hh: 12h
 */
public class CalendarUtils {
    public static LocalDate selectedDate; //Ngày hiện tại được chọn (luôn duy trì 1 ngày được select, vì vậy để final)
    public static LocalDate selectedDateOnMonthViewDialog;

    //1. Lấy String date theo định dạng (ngày tháng năm)
    public static String formattedDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return date.format(formatter);
    }

    //2. Lấy String định dạng giờ phút giây (buổi sáng chiều nếu cần)
    public static String formattedTime(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm"); //HH: là định dạng 24h | hh:mm a -> giờ theo khung 12h và phút, buổi trong ngày
        return time.format(formatter);
    }

    //Lấy String định dạng cả ngày tháng và giờ
    public static String formattedDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"); //HH: là định dạng 24h | hh:mm a -> giờ theo khung 12h và phút, buổi trong ngày
        return dateTime.format(formatter);
    }
    public static String formattedDateTimeOnlyDate(LocalDateTime dateTime, int dateFormat) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        //Chia định dạng theo các trường hợp
        if(dateFormat == 1){
            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        } else if (dateFormat == 2) {
            formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        } else if (dateFormat == 3) {
            formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        }

        return dateTime.format(formatter);
    }

    //3.1 Lấy String định dạng ngày, tháng, năm | Chú ý có thể đổi định dạng chung cho user ở hàm này (Bằng cách chia các trường hợp)
    public static String monthYearFromDate(LocalDate date, int dateFormat) {
        //Tạo date format cơ bản
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        //Chia định dạng theo các trường hợp
        if(dateFormat == 1){
            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        } else if (dateFormat == 2) {
            formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        } else if (dateFormat == 3) {
            formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        }

        //trả về string của date theo format
        return date.format(formatter);
    }

    //3.2 Lấy String định dạng mình tháng, năm
    public static String onlyMonthYearFromDate(LocalDate date, int dateFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");

        //Chia định dạng theo các trường hợp
        if(dateFormat == 1 || dateFormat == 2){
            formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        } else if (dateFormat == 3) {
            formatter = DateTimeFormatter.ofPattern("yyyy/MM");
        }

        return date.format(formatter);
    }

    //4.1 Lấy danh sách ngày trong tháng (cách cũ của form tham khảo - load 42 ngày)
//    public static ArrayList<LocalDate> daysInMonthArray(LocalDate date) {
//        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();
//
//        //Lấy tháng trong năm theo ngày đưa vào
//        YearMonth yearMonth = YearMonth.from(date);
//
//        //Số ngày trong tháng bằng chiều dài của tháng
//        int daysInMonth = yearMonth.lengthOfMonth();
//
//        //Ngày đầu tiên của tháng
//        LocalDate startOfMonth = date.withDayOfMonth(1);
//
//        //Ngày cuối cùng của tháng
////        LocalDate endOfMonth = date.withDayOfMonth(date.getMonth().length(date.isLeapYear()));
//
//        //Số ngày trong tuần đầu
//        int dayOfWeek = startOfMonth.getDayOfWeek().getValue();
//
//        //Lấy số ngày của tháng vào list (đang lấy 6 tuần vì nhiều nhất là số ngày trải dài trong 6 tuần)
//        for(int i = 1; i <= 42; i++) {
//            //Nếu ngày nhỏ hơn tuần đầu hoặc vượt quá số ngày trong tháng + tuần đầu -> add null
//            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek)
//                daysInMonthArray.add(null);
//            else
//                daysInMonthArray.add(LocalDate.of(date.getYear(),date.getMonth(),i - dayOfWeek));
//        }
//        return  daysInMonthArray;
//    }

    //4.2 Lấy danh sách ngày trong tháng theo tuần (theo vào ngày truyền vào - ngày đang được select)
    public static ArrayList<LocalDate> daysInMonthFollowWeek(LocalDate date, int startWeek) {
        //1. Tạo mảng sẽ lấy
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();

        //2. Ngày đầu tiên của tháng. Ngày cuối cùng của tháng
        LocalDate startOfMonth = date.withDayOfMonth(1);
        LocalDate endOfMonth = date.withDayOfMonth(date.getMonth().length(date.isLeapYear()));

        //3. Tuần đầu của tháng. Tuần cuối của tháng
        ArrayList<LocalDate> firtsWeek = daysInWeekArray(startOfMonth, startWeek);
        ArrayList<LocalDate> endWeek = daysInWeekArray(endOfMonth, startWeek);

        //4. Thêm ngày từ đầu tuần đầu -> đến ngày cuối của tuần cuối vào mảng
        LocalDate startOfFirtsWeek = firtsWeek.get(0);
        LocalDate endOfEndWeek = endWeek.get(endWeek.size() - 1);

        //Khi ngày đầu còn đứng trước hoặc trùng ngày cuối thì thêm vào mảng
        while (startOfFirtsWeek.isBefore(endOfEndWeek) || startOfFirtsWeek.equals(endOfEndWeek)){

            daysInMonthArray.add(startOfFirtsWeek);

            startOfFirtsWeek = startOfFirtsWeek.plusDays(1);
        }

        //Trả về mảng đủ tuần của tháng
        return  daysInMonthArray;
    }

    //4.2 Lấy danh sách ngày trong 3 tuần (theo vào ngày truyền vào - ngày đang được select)
    public static ArrayList<LocalDate> daysIn3Week(LocalDate date, int startWeek) {
        //1. Tạo mảng sẽ lấy
        ArrayList<LocalDate> daysIn3Week = new ArrayList<>();

        //2. Tuần hiện tại. Ngày đầu tuần trước. Ngày cuối tuần sau
        ArrayList<LocalDate> currentWeek = daysInWeekArray(date, startWeek);
        LocalDate startOfWeekBefore = currentWeek.get(0).minusWeeks(1);
        LocalDate endOfWeekAfter = currentWeek.get(currentWeek.size() - 1).plusWeeks(1);

        //4. Thêm ngày từ đầu tuần trước đến ngày cuối tuần sau vào list
        //Khi ngày đầu còn đứng trước hoặc trùng ngày cuối thì thêm vào mảng
        while (startOfWeekBefore.isBefore(endOfWeekAfter) || startOfWeekBefore.equals(endOfWeekAfter)){

            //Thêm ngày đầu tuần trước vào list và tăng ngày đầu tuần trước dần về ngày cuối tuần sau
            daysIn3Week.add(startOfWeekBefore);
            startOfWeekBefore = startOfWeekBefore.plusDays(1);
        }

        //Trả về mảng đủ tuần của tháng
        return  daysIn3Week;
    }

    //5. Lấy danh sách ngày trong tuần (theo ngày đưa vào), chọn ngày đầu tuần (1: CN, 2: T2)
    public static ArrayList<LocalDate> daysInWeekArray(LocalDate date, int startWeek){
        //Tạo một danh sách LocalDate trống
        ArrayList<LocalDate> daysInWeek = new ArrayList<>();

        //Tìm ngày chủ nhật (ngày đánh dấu mốc) đứng trước (hoặc trùng) ngày được chọn (date) để làm ngày đầu tuần và tra cứu lấy danh sách (1: CN, 2: Thứ 2 ...)
        LocalDate startDate = sundayBeforeDate(date, startWeek);

        //Lấy ngày kết thúc của tuần (sẽ lấy) là ngày tại 1 tuần sau đó (Vì đã có ngày đầu tuần)
        assert startDate != null; //điều kiện
        LocalDate endDate = startDate.plusWeeks(1);

        //Đưa ngày từ startDate đến endDate vào danh sách của tuần (Khi startDate còn đứng trước endDate)
        while (startDate.isBefore(endDate)) {
            daysInWeek.add(startDate);
            startDate = startDate.plusDays(1);
        }

        //Trả về list ngày của 1 tuần
        return daysInWeek;
    }

    //6. Tính ngày chủ nhật (gần nhất TRƯỚC ngày được chọn)
    private static LocalDate sundayBeforeDate(LocalDate current, int startWeek) {
        //Lấy ngày mà cách ngày đưa vào (current) 1 tuần để làm mốc tìm ngày chủ nhật
        LocalDate oneWeekAgo = current.minusWeeks(1);

        //Tìm ngày chủ nhật từ ngày oneWeekAgo đến ngày current (Vòng lặp tìm với điều kiện: Khi oneWeekAgo còn đứng trước current, hay theo cấu trúc lệnh là current đứng sau oneWeekAgo)
        while (current.isAfter(oneWeekAgo)){

            //Nếu ngày đưa vào để tra là ngày chủ nhật sẵn thì giữ nguyên (Ý nghĩa câu lệnh: lấy tên ngày của current để so sánh với tên ngày chủ nhật)
            if(startWeek == 1 && current.getDayOfWeek() == DayOfWeek.SUNDAY) {
                return current;
            }
            if(startWeek == 2 && current.getDayOfWeek() == DayOfWeek.MONDAY) {
                return current;
            }
            if(startWeek == 3 && current.getDayOfWeek() == DayOfWeek.TUESDAY) {
                return current;
            }
            if(startWeek == 4 && current.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
                return current;
            }
            if(startWeek == 5 && current.getDayOfWeek() == DayOfWeek.THURSDAY) {
                return current;
            }
            if(startWeek == 6 && current.getDayOfWeek() == DayOfWeek.FRIDAY) {
                return current;
            }
            if(startWeek == 7 && current.getDayOfWeek() == DayOfWeek.SATURDAY) {
                return current;
            }

            //Nếu không phải ngày chủ nhật (hay ngày đánh dấu mốc) thì trừ current đi 1 ngày (cho đến khi tìm được ngày chủ nhật trước khi về oneWeekAgo)
            current = current.minusDays(1);
        }

        return null;
    }

    //7. Tính ngày T2 (gần nhất trước đó) dựa vào ngày được chọn (Có thể gộp hai hàm 6 và 7 khi có nhiều hơn lựa chọn ngày đầu tuần)
    private static LocalDate mondayBeforeDate(LocalDate dayMark) {

        //Lấy ngày mà cách ngày đưa vào (darMark) 1 tuần trước để làm mốc tìm ngày
        LocalDate oneWeekAgo = dayMark.minusWeeks(1);

        //Tìm ngày T2 từ ngày oneWeekAgo đến ngày darMark (Vòng lặp tìm với điều kiện: Khi oneWeekAgo còn đứng trước current, hay theo cấu trúc lệnh là current đứng sau oneWeekAgo)
        while (dayMark.isAfter(oneWeekAgo)){

            //Nếu ngày đưa vào để tra là ngày T2 sẵn thì giữ nguyên (Ý nghĩa câu lệnh: lấy tên ngày của current để so sánh với tên ngày T2)
            if(dayMark.getDayOfWeek() == DayOfWeek.MONDAY) {
                return dayMark;
            }

            //Nếu không phải thì trừ current đi 1 ngày (cho đến khi tìm được ngày T2 trước khi về oneWeekAgo)
            dayMark = dayMark.minusDays(1);
        }

        return null;
    }


}
