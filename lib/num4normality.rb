require 'java'
require 'num4normality.jar'
require 'jfreechart-1.5.4.jar'
require 'commons-math3-3.6.1.jar'

java_import 'Normality'

# 正規性の検定
#  (Apache commoms math3使用)
module Num4NormalityLib
    class << self
        # Q-Qプロット
        #
        # @overload qqplot(dname, xi)
        #   @param [String] dname データ名
        #   @param  [Array]  xi データ(double[])
        #   @return [void]  qqplot.jpegファイルを出力
        # @example
        #   xi = [320, 240, 402, 325, 440, 286, 362, 281, 560, 212, 198, 209, 374]
        #   Num4NormalityLib.qqplot("LDH", xi)
        #   => qqplot.jpeg
        # @note
        #   グラフは、jfreechartを使用
        def qqplot(dname, xi)
            Normality.qqplot(dname, xi.to_java(Java::double))
        end
        # コルモゴルフ・スミルノフ検定プロット(1標本)
        #
        # @overload ksplot(dname, xi)
        #   @param [String] dname データ名
        #   @param  [Array]  xi データ(double[])
        #   @return [void]  ksplot.jpegファイルを出力
        # @example
        #   xi = [320, 240, 402, 325, 440, 286, 362, 281, 560, 212, 198, 209, 374]
        #   Num4NormalityLib.ksplot("LDH", xi)
        #   => kstest.jpeg
        # @note
        #   グラフは、jfreechartを使用
        def ksplot(dname, xi)
            Normality.ksplot(dname, xi.to_java(Java::double))
        end
        # Q-Q and コルモゴルフ・スミルノフ検定プロット(1標本)
        #
        # @overload qqksplot(dname, xi)
        #   @param [String] dname データ名
        #   @param  [Array]  xi データ(double[])
        #   @return [void]  qqksplot.jpegファイルを出力
        # @example
        #   xi = [320, 240, 402, 325, 440, 286, 362, 281, 560, 212, 198, 209, 374]
        #   Num4NormalityLib.qqksplot("LDH", xi)
        #   => qqksplot.jpeg
        # @note
        #   グラフは、jfreechartを使用
        def qqksplot(dname, xi)
            Normality.qqksplot(dname, xi.to_java(Java::double))
        end
        # P-Pプロット
        #
        # @overload ppplot(dname, xi)
        #   @param [String] dname データ名
        #   @param  [Array]  xi データ(double[])
        #   @return [void]  ppplot.jpegファイルを出力
        # @example
        #   xi = [320, 240, 402, 325, 440, 286, 362, 281, 560, 212, 198, 209, 374]
        #   Num4NormalityLib.ppplot("LDH", xi)
        #   => ppplot.jpeg
        # @note
        #   グラフは、jfreechartを使用
        def ppplot(dname, xi)
            Normality.ppplot(dname, xi.to_java(Java::double))
        end
        # P-P and コルモゴルフ・スミルノフ検定プロット(1標本)
        #
        # @overload ppksplot(dname, xi)
        #   @param [String] dname データ名
        #   @param  [Array]  xi データ(double[])
        #   @return [void]  ppplot.jpegファイルを出力
        # @example
        #   xi = [320, 240, 402, 325, 440, 286, 362, 281, 560, 212, 198, 209, 374]
        #   Num4NormalityLib.ppksplot("LDH", xi)
        #   => ppksplot.jpeg
        # @note
        #   グラフは、jfreechartを使用
        def ppksplot(dname, xi)
            Normality.ppksplot(dname, xi.to_java(Java::double))
        end
        # コルモゴルフ・スミルノフ検定(1標本)
        #
        # @overload kstest(xi)
        #   @param  [Array]  xi データ(double[])
        #   @return [boolean] 検定結果(true:棄却域内 false:棄却域外)
        # @example
        #   xi = [320, 240, 402, 325, 440, 286, 362, 281, 560, 212, 198, 209, 374]
        #   Num4NormalityLib.kstest(xi)
        #   => false
        def kstest(xi)
            Normality.kstest(xi.to_java(Java::double))
        end

        # タコスディーノ検定(歪度)
        #
        # @overload skewnesstest(xi)
        #   @param  [Array]  xi データ(double[])
        #   @return [boolean] 検定結果(true:棄却域内 false:棄却域外)
        # @example
        #   xi = [320, 240, 402, 325, 440, 286, 362, 281, 560, 212, 198, 209, 374]
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
        #   xi = [320, 240, 402, 325, 440, 286, 362, 281, 560, 212, 198, 209, 374]
        #   Num4NormalityLib.kurtosistest(xi)
        #   => false
        def kurtosistest(xi)
            Normality.kurtosistest(xi.to_java(Java::double))
        end
        # オムニバス検定
        #
        # @overload omnibustest(xi)
        #   @param  [Array]  xi データ(double[])
        #   @return [boolean] 検定結果(true:棄却域内 false:棄却域外)
        # @example
        #   xi = [320, 240, 402, 325, 440, 286, 362, 281, 560, 212, 198, 209, 374]
        #   Num4NormalityLib.omnibustest(xi)
        #   => false
        def omnibustest(xi)
             Normality.omnibustest(xi.to_java(Java::double))
        end
    end
end

