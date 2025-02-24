# 小数点桁四捨五入
module MyFileMatcher
  class Matcher
    def initialize(expected)
      @expected = expected
    end

    def matches?(actual)
        return File.exist?(@expected)
    end

    def failure_message
      "#{@expected} expected but got #{@actual}"
    end
  end
  def is_exist(expected)
    Matcher.new(expected)
  end
end

