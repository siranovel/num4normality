require 'java'
require 'num4normality.jar'
require 'jfreechart-1.5.4.jar'
require 'commons-math3-3.6.1.jar'

java_import 'Normality'

module Num4NormalityLib
    class << self
        # Q-Qプロット
        #
        # @overload qqplot(dname, xi)
        #   @param [String] dname データ名
        #   @param  [Array]  xi データ(double[])
        #   @return [void]  qqplot.jpegファイルを出力
        # @example
        #   xi = [320, 240, 402, 325, 440, 286, 362, 281, 560, 212, 198, 209]
        #   Num4NormalityLib.qqplot("LDH", xi)
        #   => qqplot.jpeg
        def qqplot(dname, xi)
            Normality.qqplot(dname, xi.to_java(Java::double))
        end
        # コルモゴルフ・スミルノフ検定
        #
        # @overload kstest(dname, xi)
        #   @param [String] dname データ名
        #   @param  [Array]  xi データ(double[])
        #   @return [void]  kstest.jpegファイルを出力
        # @example
        #   xi = [320, 240, 402, 325, 440, 286, 362, 281, 560, 212, 198, 209]
        #   Num4NormalityLib.kstest("LDH", xi)
        #   => kstest.jpeg
        def kstest(dname, xi)
            Normality.kstest(dname, xi.to_java(Java::double))
        end
        # タコスディーノ検定(歪度)
        #
        # @overload skewnesstest(xi)
        #   @param  [Array]  xi データ(double[])
        #   @return [boolean] 検定結果(true:棄却域内 false:棄却域外)
        # @example
        #   xi = [320, 240, 402, 325, 440, 286, 362, 281, 560, 212, 198, 209]
        #   Num4NormalityLib.skewnesstest(xi)
        #   => false
        def skewnesstest(xi)
            Normality.skewnesstest(xi.to_java(Java::double))
        end
        # タコスディーノ検定(尖度)
        #
        # @overload kurtosistest(xi)
        #   @param  [Array]  xi データ(double[])
        #   @return [boolean] 検定結果(true:棄却域内 false:棄却域外)
        # @example
        #   xi = [320, 240, 402, 325, 440, 286, 362, 281, 560, 212, 198, 209]
        #   Num4NormalityLib.kurtosistest(xi)
        #   => false
        def kurtosistest(xi)
            Normality.kurtosistest(xi.to_java(Java::double))
        end
    end
end

