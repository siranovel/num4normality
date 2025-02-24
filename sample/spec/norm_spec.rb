require 'spec_helper'
require 'num4normality'

RSpec.describe Num4NormalityLib do
    before do
        @xi = [320, 240, 402, 325, 440, 286, 362, 281, 560, 212, 198, 209, 374]
    end
    it '#qqplot' do
        expect(
            Num4NormalityLib.qqplot("LDH", @xi)
        ).to is_exist("qqplot.jpeg")
    end
    it '#ksplot' do
        expect(
            Num4NormalityLib.ksplot("LDH", @xi)
        ).to is_exist("ksplot.jpeg")
    end
    it '#qqksplot' do
        expect(
            Num4NormalityLib.qqksplot("LDH", @xi)
        ).to is_exist("qqksplot.jpeg")
    end
    it '#ppplot' do
        expect(
            Num4NormalityLib.ppplot("LDH", @xi)
        ).to is_exist("ppplot.jpeg")
    end
    it '#ppksplot' do
        expect(
            Num4NormalityLib.ppksplot("LDH", @xi)
        ).to is_exist("ppksplot.jpeg")
    end
    it '#kstest' do
        expect(
            Num4NormalityLib.kstest(@xi)
        ).to eq false
    end
    it '#skewnesstest' do
        expect(
            Num4NormalityLib.skewnesstest(@xi)
        ).to eq false
    end
    it '#kurtosistest' do
        expect(
            Num4NormalityLib.kurtosistest(@xi)
        ).to eq false
    end
    it '#omnibustest' do
        expect(
            Num4NormalityLib.omnibustest(@xi)
        ).to eq false
    end
    it '#adtest' do
        expect(
            Num4NormalityLib.adtest(@xi)
        ).to eq false
    end
    it '#jbtest' do
        expect(
            Num4NormalityLib.jbtest(@xi)
        ).to eq false
    end
end
