package com.example.simgizi.api;

public class config {
    public final static String API = "simgizi.online";
//    public final static String API_LOCAL = "192.168.1.11/SIMGizi";

    public static final String ipserver = "http://" + API + "/php/api/";
//    public static final String ipserverlocal = "http://" + API_LOCAL + "/php/api/";
    public static final String api_get_distribusi = ipserver + "api_get_distribusi.php";
    public static final String api_update_status_pengiriman = ipserver + "api_update_status_pengiriman.php";
    public static final String api_crud_distribusi = ipserver + "api_crud_distribusi.php";
    public static final String api_get_sekolah = ipserver + "api_get_sekolah.php";
    public static final String api_get_dashboard_stats = ipserver + "api_get_dashboard_stats.php";
    public static final String api_crud_stok = ipserver + "api_crud_stok.php";
    public static final String api_get_riwayat = ipserver + "api_get_riwayat.php";
    public static final String api_get_stok_harian = ipserver + "api_get_stok_harian.php";
    public static final String api_login = ipserver + "api_login.php";
    public static final String api_filter = ipserver + "api_filter.php";
    public static final String api_update_lokasi = ipserver + "api_update_lokasi.php";
    public static final String api_get_evaluasi = ipserver + "api_get_evaluasi.php";
    public static final String api_get_bahan_makanan = ipserver + "api_get_bahan_makanan.php";
    public static final String api_save_menu_stok = ipserver + "api_save_menu_stok.php";

    public static final String base_url_image = "http://" + API + "/uploads/";

}
