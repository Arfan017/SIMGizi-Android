package com.example.simgizi.api;

public class config {
    public final static String API = "192.168.1.6";

    public static final String iplocal = "http://" + API + "/MonitGizi/php/api/";
    public static final String api_get_distribusi = iplocal + "api_get_distribusi.php";
    public static final String api_update_status_pengiriman = iplocal + "api_update_status_pengiriman.php";
    public static final String api_crud_distribusi = iplocal + "api_crud_distribusi.php";
    public static final String api_get_sekolah = iplocal + "api_get_sekolah.php";
    public static final String api_get_dashboard_stats = iplocal + "api_get_dashboard_stats.php";
    public static final String api_crud_stok = iplocal + "api_crud_stok.php";
    public static final String api_get_riwayat = iplocal + "api_get_riwayat.php";
    public static final String api_get_stok_harian = iplocal + "api_get_stok_harian.php";
    public static final String api_login = iplocal + "api_login.php";

    public static final String base_url_image = "http://" + API + "/MonitGizi/uploads/";

}
