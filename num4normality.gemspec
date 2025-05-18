Gem::Specification.new do |s|
  s.name          = 'num4normality'
  s.version       = '0.1.1'
  s.date          = '2025-05-15'
  s.summary       = "num for normality"
  s.description   = "numerical solution for test of normality."
  s.authors       = ["siranovel"]
  s.email         = "siranovel@gmail.com"
  s.homepage      = "http://github.com/siranovel/num4normality"
  s.license       = "MIT"
  s.required_ruby_version = ">= 3.0"
  s.files         = ["LICENSE", "Gemfile", "CHANGELOG.md"]
  s.files         += Dir.glob("{lib,ext}/**/*")
  s.extensions  = %w[Rakefile]
  s.add_dependency 'rake', '~> 13', '>= 13.0.6'
  s.add_development_dependency 'rake-compiler', '~> 1.3', '>= 1.3.0'
end

