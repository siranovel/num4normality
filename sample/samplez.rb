dname = File.dirname(__FILE__) + '/'
require 'num4normality'
require dname + './mymatcher'

RSpec.describe Num4NormalityLib do
    it '#qqplot' do
        xi = [320, 240, 402, 325, 440, 286, 362, 281, 560, 212, 198, 209]
        expect(
            Num4NormalityLib.qqplot("LDH", xi)
        ).to is_exist("qqplot.jpeg")
    end
    it '#kstest' do
        xi = [320, 240, 402, 325, 400, 286, 362, 281, 560, 212, 198, 209]
        expect(
            Num4NormalityLib.kstest("LDH", xi)
        ).to is_exist("kstest.jpeg")
    end
    it '#skewnesstest' do
        xi = [320, 240, 402, 325, 400, 286, 362, 281, 560, 212, 198, 209]
        expect(
            Num4NormalityLib.skewnesstest(xi)
        ).to eq false
    end
    it '#kurtosistest' do
        xi = [320, 240, 402, 325, 400, 286, 362, 281, 560, 212, 198, 209]
        expect(
            Num4NormalityLib.kurtosistest(xi)
        ).to eq true
    end
end
