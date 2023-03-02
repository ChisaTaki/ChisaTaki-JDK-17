package dev.kurumiDisciples.chisataki.enums;

public enum GifEnum {
  CHISATO_GIGGLE("https://media.tenor.com/n_RaZkvRolcAAAAC/lycoris-recoil.gif"),
  CHISATO_SIP("https://media.tenor.com/dwqn-DDq0GkAAAAd/chisato-nishikigi-lycoris-recoil.gif"),
  CHISATO_UNAMUSED("https://media.tenor.com/fwqF3QgHTwoAAAAC/lycoris-recoil-%E3%83%AA%E3%82%B3%E3%83%AA%E3%82%B9%E3%83%AA%E3%82%B3%E3%82%A4%E3%83%AB.gif"),
  JANKEN_ROCK_PAPER("https://media.tenor.com/WwTMs56vEdwAAAAC/chisa-taki-janken.gif"),
  JANKEN_ROCK_SCISSORS("https://media.tenor.com/W67BM2peuHsAAAAC/chisa-taki-janken.gif"),
  JANKEN_PAPER_SCISSORS("https://media.tenor.com/8uw8xPRf_dEAAAAC/chisa-taki-janken.gif"),
  ROCK_COMES_FIRST(
      "https://media.discordapp.net/attachments/1044304571767472199/1061442456022954116/beating-your-waifu-at-janken.gif?width=1193&height=671"),
  TAKINA_CELEBRATION("https://media.tenor.com/CVX1EcTJEMYAAAAC/lycoris-recoil-takina.gif"),
  TAKINA_LOSES_RPS("https://media.tenor.com/tkA1_999XjgAAAAd/lycoris-recoil-takina.gif");

  private String url;

  private GifEnum(String url) {
    this.url = url;
  }

  public String getUrl() {
    return this.url;
  }
}