package org.example.autosuggest;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonTest {
    private static final ObjectMapper mapper = new ObjectMapper();
    //final static String line = "{\"sku\":1053196,\"name\":\"Hot Wheels: Beat That - PRE-OWNED - Nintendo DS\",\"type\":\"Game\",\"price\":9.99,\"upc\":\"799007751908\",\"category\":[{\"id\":\"abcat0700000\",\"name\":\"Video Games\"},{\"id\":\"pcmcat232900050017\",\"name\":\"Pre-Owned Games\"}],\"shipping\":3.99,\"description\":\"Show off your racing skills\",\"manufacturer\":\"Activision\",\"model\":null,\"url\":\"http://www.bestbuy.com/site/hot-wheels-beat-that-pre-owned-nintendo-ds/1053196.p?id=1218215103698&skuId=1053196&cmp=RMXCC\",\"image\":\"http://img.bbystatic.com/BestBuy_US/images/products/1053/1053196_sa.jpg\"},";
    //final static String line = "{\"sku\":7787567,\"name\":null,\"type\":\"HardGood\",\"price\":34.99,\"upc\":\"799007014164\",\"category\":[{\"id\":\"pcmcat113100050015\",\"name\":\"Carfi Instore Only\"}],\"shipping\":\"\",\"description\":\"GPS CAR INSTALLATION\",\"manufacturer\":\"INSTALL 011\",\"model\":\"INSTALL GP\",\"url\":\"nullCC\",\"image\":\"http://img.bbystatic.com/BestBuy_US/images/products/nonsku/default_hardlines_m.gif\"},";
    final static String line = "{\"sku\":5493005,\"name\":\"null: Second Son - PRE-OWNED - PlayStation 4\",\"type\":\"Game\",\"price\":14.99,\"upc\":\"799007838203\",\"category\":[{\"id\":\"abcat0700000\",\"name\":\"Video Games\"},{\"id\":\"pcmcat232900050017\",\"name\":\"Pre-Owned Games\"}],\"shipping\":3.99,\"description\":\"Take on the Department of Unified Protection as superhuman Delsin Rowe\",\"manufacturer\":\"Sony\",\"model\":null,\"url\":\"http://www.bestbuy.com/site/infamous-second-son-pre-owned-playstation-4/5493005.p?id=1219133625600&skuId=5493005&cmp=RMXCC\",\"image\":\"http://img.bbystatic.com/BestBuy_US/images/products/5493/5493005_sa.jpg\"},";
    final static String line4 = "{\"sku\":2757341,\"name\":\"3-Year Performance Service Plan (Carry-In) Laptop\",\"type\":\"BlackTie\",\"price\":219.99,\"upc\":\"400027573417\",\"category\":[{\"id\":\"abcat0700000\",\"name\":\"Video Games\"},{\"id\":\"abcat0715000\",\"name\":\"Video Game Accessories\"},{\"id\":\"pcmcat249400050012\",\"name\":\"Gaming Eyewear\"}],\"shipping\":0,\"description\":\"&nbsp\",\"url\":\"http://www.bestbuy.com/site/3-year-performance-service-plan-carry-in-laptop/2757341.p?id=1051384048696&skuId=2757341&cmp=RMXCC\",\"image\":\"http://img.bbystatic.com/BestBuy_US/images/products/nonsku/default_hardlines_l.jpg\"},";
    final static String line5 = "{\"sku\":8214029,\"name\":\"Songs from an Open Book - CD\",\"type\":\"Music\",\"price\":11.99,\"upc\":\"759707140327\",\"category\":[{\"id\":\"abcat0600000\",\"name\":\"Movies & Music\"},{\"id\":\"cat02001\",\"name\":\"Music (CDs & Vinyl)\"},{\"id\":\"cat02009\",\"name\":\"Pop\"}],\"shipping\":3.99,\"description\":\"Modern Vintage (Only @ Best Buy) w/T-Shirt\",\"manufacturer\":\"Brando\",\"url\":\"http://www.bestbuy.com/site/songs-from-an-open-book-cd/8214029.p?id=3281102&skuId=8214029&cmp=RMXCC\",\"image\":\"http://img.bbystatic.com/BestBuy_US/images/products/nonsku/default_music_l.jpg\"},";
    public static void main(String[] args) {

        try {
            Product p = mapper.readValue(line5.replaceAll("^\\[", "").replace(",\\s+*$", "").replaceAll(":\\s*null", ":\"\""), Product.class);
            System.out.println(p.getSku());
            System.out.println(p.getModel());
            System.out.println(p.getName());
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
    }

}