package com.apabi.flow.crawlTask.util;

import java.util.Random;

/**
 * @Author pipi
 * @Date 2018/10/23 10:58
 **/
public class DoubanCookieUtils {
    private static String[] cookies = {
            "ll=\"131616\"; bid=xII_s6X3G5k",
            "ll=\"108113\"; bid=Rw27McWy6oU",
            "ll=\"108162\"; bid=jgQe5Ep2aRI",
            "ll=\"108233\"; bid=5GGeDcl1Z_I",
            "ll=\"108218\"; bid=lW38AVx2-nE",
            "ll=\"108255\"; bid=jqH-rVBcTRU",
            "ll=\"108218\"; bid=mNnMjZ0blag",
            "ll=\"131495\"; bid=Y9lDG9EGZng",
            "ll=\"108218\"; bid=c3ygE0q0SeU",
            "ll=\"108218\"; bid=Kjnm104yLwQ",
            "ll=\"108218\"; bid=UX0jQKQvkTM",
            "ll=\"108172\"; bid=_g1UogKWp70",
            "ll=\"108149\"; bid=kWeyhA7jLxE",
            "ll=\"108125\"; bid=uIBI_kcxLtQ",
            "ll=\"118278\"; bid=BIMRe2trFaI",
            "ll=\"108161\"; bid=k9WNc3tA3X8",
            "ll=\"131616\"; bid=-FVypZRDRYM",
            "ll=\"108113\"; bid=LJMk5e3RhbI",
            "ll=\"108162\"; bid=GnZvwZ67pk4",
            "ll=\"108218\"; bid=bHk_shAKoP4",
            "ll=\"108162\"; bid=ELF4ibLFp8Y",
            "ll=\"108125\"; bid=UB7rG_oabMQ",
            "ll=\"108162\"; bid=8xu9tZ7jaqw",
            "ll=\"108164\"; bid=sdW8-Mu966c",
            "ll=\"108218\"; bid=pr3qS2TKTJU",
            "ll=\"118418\"; bid=RG3MzKy9-Ro",
            "ll=\"108218\"; bid=fv56pWyGJg0",
            "ll=\"108161\"; bid=QuRaflkfIrs",
            "ll=\"131616\"; bid=PBhYfEQSeGE",
            "ll=\"108255\"; bid=o7_AWLtWRyQ",
            "ll=\"108100\"; bid=gnXl7V6riWY",
            "ll=\"108218\"; bid=3b_TxjcOig8",
            "ll=\"108197\"; bid=De9mwUDeP7c",
            "ll=\"108218\"; bid=Ch-B8X5qTcU",
            "ll=\"108115\"; bid=DYaXkKFcI0k",
            "ll=\"108218\"; bid=BEhvtoJehMI",
            "ll=\"108218\"; bid=eW1iPrOhtFc",
            "ll=\"108167\"; bid=P4cWMTqZF3o",
            "ll=\"108115\"; bid=geRShkK0XbE",
            "ll=\"108218\"; bid=R-lnNf-1d-k",
            "ll=\"108288\"; bid=Wyc5imR5LPc",
            "ll=\"108255\"; bid=LnJwv7rYu38",
            "ll=\"108164\"; bid=rw1cOQx81oo",
            "ll=\"108218\"; bid=gxykF1IoMM0",
            "ll=\"108162\"; bid=ANcu2EgDQZQ",
            "ll=\"108164\"; bid=IHxsaEnC2WY",
            "ll=\"108162\"; bid=fkTphhWMA_w",
            "ll=\"108255\"; bid=-wPZP6dD-MI",
            "ll=\"108218\"; bid=cIIJxITjmVw",
            "ll=\"108150\"; bid=m4t5Tppe8lU",
            "ll=\"108189\"; bid=AlEvoFdahpk",
            "ll=\"108162\"; bid=Krs7jM1SkY8",
            "ll=\"108162\"; bid=avFLT8bP3hs",
            "ll=\"108162\"; bid=XT2soFoYzMo",
            "ll=\"108113\"; bid=aR4oRUuQP2U",
            "ll=\"108113\"; bid=IQa03Q0B3zE",
            "ll=\"108132\"; bid=oK7a1ewAVzs",
            "ll=\"108162\"; bid=2PXrzO-c6AM",
            "ll=\"108113\"; bid=ubphFjdI8O4",
            "ll=\"108236\"; bid=-BPMHQY3LLQ",
            "ll=\"108197\"; bid=mAfGEmr5o1U",
            "ll=\"108125\"; bid=TPafb2Horw4",
            "ll=\"108113\"; bid=t1SuT-0DGfI",
            "ll=\"108113\"; bid=2i7j6cbtCek",
            "ll=\"108255\"; bid=bUuZF7HlbyQ",
            "ll=\"108218\"; bid=fc7PT-8nMJQ",
            "ll=\"108197\"; bid=blSQUTHm2bQ"
    };

    public static String getCookie(){
        int size = cookies.length;
        Random random = new Random();
        int index = random.nextInt(size);
        return cookies[index];
    }
}
